package com.example.classroom_reservation_system.member.service;

import com.example.classroom_reservation_system.auth.dto.response.TokenResponse;
import com.example.classroom_reservation_system.auth.service.RefreshTokenService;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.config.jwt.JwtProperties;
import com.example.classroom_reservation_system.config.jwt.JwtUtil;
import com.example.classroom_reservation_system.member.dto.request.MyInfoUpdateRequest;
import com.example.classroom_reservation_system.member.dto.request.PasswordChangeRequest;
import com.example.classroom_reservation_system.member.dto.response.MyInfoResponse;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.member.repository.AdminRepository;
import com.example.classroom_reservation_system.member.repository.MemberRepository;
import com.example.classroom_reservation_system.member.repository.ProfessorRepository;
import com.example.classroom_reservation_system.member.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final AdminRepository adminRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    /**
     * UUID로 회원 찾기
     * @param memberUuid
     */
    public Member findByMemberUuid(String memberUuid) {
        return memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 아이디로 회원 찾기
     * @param id
     */
    public Optional<Member> findMemberById(String id) {
        return studentRepository.findByStudentId(id)
                .map(Member.class::cast)
                .or(() -> professorRepository.findByProfessorId(id).map(Member.class::cast))
                .or(() -> adminRepository.findByAdminId(id).map(Member.class::cast));
    }

    /**
     * 현재 로그인한 사용자의 계정을 삭제
     * @param memberUuid 현재 로그인한 사용자의 UUID
     */
    @Transactional
    public void deleteMyAccount(String memberUuid) {
        Member member = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        memberRepository.delete(member);
    }

    /**
     * 내 정보 조회
     * @param memberUuid 현재 로그인한 사용자의 UUID
     */
    public MyInfoResponse getMyInfo(String memberUuid) {
        Member member = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return MyInfoResponse.from(member);
    }

    /**
     * 내 정보 수정
     * @param memberUuid 현재 로그인한 사용자의 UUID
     * @param request 수정할 정보
     */
    @Transactional
    public TokenResponse updateMyInfoAndReissueTokens(String memberUuid, MyInfoUpdateRequest request) {
        Member member = findByMemberUuid(memberUuid);

        String newName = request.getName();
        String newEmail = request.getEmail();

        // 정보 업데이트
        if (newName != null && !newName.isBlank()) {
            member.changeName(newName);
        }

        if (newEmail != null && !newEmail.isBlank() && !member.getEmail().equals(newEmail)) {
            // 이메일 중복 검사
            if (memberRepository.existsByEmail(newEmail)) {
                throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
            }

            member.changeEmail(newEmail);
        }

        // 수정된 정보로 새 토큰 발급
        String newAccessToken = jwtUtil.generateAccessToken(
                member.getMemberUuid(),
                member.getId(),
                member.getName(),
                member.getRole()
        );

        String newRefreshToken = jwtUtil.generateRefreshToken(member.getMemberUuid());

        // DB의 Refresh Token 갱신
        refreshTokenService.saveRefreshToken(
                member.getMemberUuid(),
                newRefreshToken,
                jwtProperties.getRefreshTokenExpirationTime()
        );

        // TokenResponse DTO를 생성하여 반환
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    /**
     * 로그인한 사용자의 비밀번호 변경
     * @param memberUuid 현재 로그인한 사용자의 UUID
     * @param request 변경할 비밀번호 정보
     */
    @Transactional
    public void changePassword(String memberUuid, PasswordChangeRequest request) {
        Member member = findByMemberUuid(memberUuid);

        // 현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.CURRENT_PASSWORD_NOT_MATCH);
        }

        // 새 비밀번호와 확인용 비밀번호 일치 여부 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }

        // 이전 비밀번호과 동일한지 확인
        if (passwordEncoder.matches(request.getNewPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.CANNOT_USE_OLD_PASSWORD);
        }

        // 비밀번호 변경
        member.changePassword(passwordEncoder.encode(request.getNewPassword()));

        // 비밀번호 변경 후 다른 모든 기기 로그아웃 처리
        refreshTokenService.deleteByMemberUuid(memberUuid);
    }
}
