package com.example.classroom_reservation_system.member.service;

import com.example.classroom_reservation_system.auth.dto.response.TokenResponse;
import com.example.classroom_reservation_system.config.jwt.JwtUtil;
import com.example.classroom_reservation_system.config.security.CustomUserDetails;
import com.example.classroom_reservation_system.member.dto.request.MyInfoUpdateRequest;
import com.example.classroom_reservation_system.member.dto.response.MyInfoResponse;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.member.repository.AdminRepository;
import com.example.classroom_reservation_system.member.repository.MemberRepository;
import com.example.classroom_reservation_system.member.repository.ProfessorRepository;
import com.example.classroom_reservation_system.member.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final JwtUtil jwtUtil;

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

        // TokenResponse DTO를 생성하여 반환
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
