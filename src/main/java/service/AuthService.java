package service;

import com.example.classroom_reservation_system.config.JwtProperties;
import com.example.classroom_reservation_system.dto.requestDto.LoginRequest;
import com.example.classroom_reservation_system.dto.requestDto.SignUpRequest;
import com.example.classroom_reservation_system.dto.requestDto.TokenRequest;
import com.example.classroom_reservation_system.dto.responseDto.LoginResponse;
import com.example.classroom_reservation_system.dto.responseDto.TokenResponse;
import com.example.classroom_reservation_system.entity.*;
import com.example.classroom_reservation_system.exception.CustomException;
import com.example.classroom_reservation_system.exception.ErrorCode;
import com.example.classroom_reservation_system.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final MemberLoginService memberLoginService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인
     */
    public LoginResponse login(LoginRequest request) {
        // member 찾기
        Member member = memberLoginService.login(request.getId(), request.getPassword());

        // JWT 생성
        String accessToken = jwtUtil.generateAccessToken(member.getMemberUuid(), member.getId(), member.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberUuid());

        // RefreshToken 저장
        refreshTokenService.saveRefreshToken(
                member.getMemberUuid(),
                refreshToken,
                jwtProperties.getAccessTokenExpirationTime()
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    /**
     * 토큰 재발급
     */
    public TokenResponse refresh(TokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 토큰 null 또는 공백 검증
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        // 유효성 검증 (만료/변조)
        try {
            jwtUtil.validateTokenOrThrow(refreshToken);
        } catch (CustomException e) {
            if (e.getErrorCode() == ErrorCode.ACCESS_TOKEN_EXPIRED) {
                throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
            }
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // DB에 저장된 토큰인지 검증
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken);

        if (!storedToken.getToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_MISMATCH);
        }

        // 새 AccessToken 발급
        String memberUuid = storedToken.getMemberUuid();
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        Role role = Role.valueOf(jwtUtil.getRoleFromToken(refreshToken));

        String newAccessToken = jwtUtil.generateAccessToken(memberUuid, userId, role);

        return new TokenResponse(newAccessToken);
    }

    /**
     * 로그아웃: RefreshToken 삭제
     */
    public void logout(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }

        String memberUuid = authentication.getPrincipal().toString();
        refreshTokenService.deleteByMemberUuid(memberUuid);
    }

    /**
     * 회원가입
     */
    public void signUp(SignUpRequest request) {
        // ADMIN으로 회원가입 요청 차단
        if (request.getRole() == Role.ADMIN) {
            throw new CustomException(ErrorCode.INVALID_ROLE_FOR_SIGNUP);
        }

        // 중복 ID 검사
        if (memberRepository.existsByStudentIdOrProfessorIdOrAdminId(request.getId(), request.getId(), request.getId())) {
            throw new CustomException(ErrorCode.DUPLICATE_ID);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 역할에 따라 Member 생성
        Member newMember = switch (request.getRole()) {
            case STUDENT -> Student.builder()
                    .studentId(request.getId())
                    .name(request.getName())
                    .password(encodedPassword)
                    .role(Role.STUDENT)
                    .build();
            case PROFESSOR -> Professor.builder()
                    .professorId(request.getId())
                    .name(request.getName())
                    .password(encodedPassword)
                    .role(Role.PROFESSOR)
                    .build();
            default -> throw new CustomException(ErrorCode.VALIDATION_FAIL);
        };

        memberRepository.save(newMember);
    }

    /**
     * 아이디 중복 검사 로직
     * @param id
     */
    public void checkDuplicateId(String id) {
        boolean exists = memberRepository.existsByStudentIdOrProfessorIdOrAdminId(id, id, id);

        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_ID);
        }
    }
}
