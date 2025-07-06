package com.example.classroom_reservation_system.service;

import com.example.classroom_reservation_system.config.JwtProperties;
import com.example.classroom_reservation_system.dto.requestDto.LoginRequest;
import com.example.classroom_reservation_system.dto.requestDto.SignUpRequest;
import com.example.classroom_reservation_system.dto.requestDto.TokenRequest;
import com.example.classroom_reservation_system.dto.responseDto.LoginResponse;
import com.example.classroom_reservation_system.dto.responseDto.TokenResponse;
import com.example.classroom_reservation_system.entity.*;
import com.example.classroom_reservation_system.exception.CustomException;
import com.example.classroom_reservation_system.exception.ErrorCode;
import com.example.classroom_reservation_system.repository.member.AdminRepository;
import com.example.classroom_reservation_system.repository.member.ProfessorRepository;
import com.example.classroom_reservation_system.repository.member.StudentRepository;
import com.example.classroom_reservation_system.repository.token.PasswordResetTokenRepository;
import com.example.classroom_reservation_system.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.classroom_reservation_system.repository.member.MemberRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final AdminRepository adminRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final MemberLoginService memberLoginService;
    private final MailService mailService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z0-9]{6,15}$");
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
        Member member = memberService.findByMemberUuid(memberUuid);


        String newAccessToken = jwtUtil.generateAccessToken(memberUuid, member.getId(), member.getRole());

        return new TokenResponse(newAccessToken);
    }

    /**
     * 로그아웃: RefreshToken 삭제
     */
    public void logout(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new CustomException(ErrorCode.AUTHENTICATION_NOT_FOUND);
        }

        String memberUuid = authentication.getPrincipal().toString();
        refreshTokenService.deleteByMemberUuid(memberUuid);
    }

    /**
     * 회원가입
     */
    public void signUp(SignUpRequest request) {

        if (request.getRole() == null) {
            throw new CustomException(ErrorCode.VALIDATION_FAIL);
        }
        // ADMIN으로 회원가입 요청 차단
        if (request.getRole() == Role.ADMIN) {
            throw new CustomException(ErrorCode.INVALID_ROLE_FOR_SIGNUP);
        }

        // 비밀번호, 비밀번호 확인 일치 여부 체크
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }



        // 중복 검사
        validateIdFormat((request.getId()));
        checkDuplicateId(request.getId());
        checkDuplicateEmail(request.getEmail());

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 역할에 따라 Member 생성
        switch (request.getRole()) {
            case STUDENT -> {
                Student student = Student.builder()
                        .studentId(request.getId())
                        .name(request.getName())
                        .password(encodedPassword)
                        .email(request.getEmail())
                        .role(Role.STUDENT)
                        .build();

                studentRepository.save(student);
            }

            case PROFESSOR -> {
                Professor professor = Professor.builder()
                        .professorId(request.getId())
                        .name(request.getName())
                        .password(encodedPassword)
                        .email(request.getEmail())
                        .role(Role.PROFESSOR)
                        .build();

                professorRepository.save(professor);
            }

            default -> throw new CustomException(ErrorCode.VALIDATION_FAIL);
        }
    }

    /**
     * 아이디 중복 검사 로직
     * @param id
     */
    public void checkDuplicateId(String id) {
        boolean exists =
                studentRepository.existsByStudentId(id) ||
                        professorRepository.existsByProfessorId(id) ||
                        adminRepository.existsByAdminId(id);

        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_ID);
        }
    }

    /**
     * 이메일로 링크 발송
     */
    public void sendResetPasswordMail(String id){
        Optional<? extends  Member> memberOpt =
                studentRepository.findByStudentId(id).map(m -> (Member) m)
                .or(() -> professorRepository.findByProfessorId(id).map(m -> (Member) m))
                .or(() -> adminRepository.findByAdminId(id).map(m -> (Member) m));

        if (memberOpt.isEmpty()) return;

        Member member = memberOpt.get();
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);

        // 토큰 저장
        PasswordResetToken entity = PasswordResetToken.builder()
                .token(token)
                .expiresAt(expiresAt)
                .used(false)
                .member(member)
                .build();

        tokenRepository.save(entity);

        // 이메일 전송
        String link = frontendUrl + "/auth/resetPassword?token" + token;
        mailService.sendResetPasswordMail(member.getEmail(), link);
    }

    /**
     * 비밀번호 재설정
     */
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESET_TOKEN));

        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.INVALID_RESET_TOKEN);
        }

        Member member = resetToken.getMember();
        String encodedPassword = passwordEncoder.encode(newPassword);

        member.changePassword(encodedPassword);
        resetToken.use();

        tokenRepository.save(resetToken);
    }

    public void checkDuplicateEmail(String email){
        boolean exists = memberRepository.existsByEmail(email);

        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    public void validateIdFormat(String id) {
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new CustomException(ErrorCode.INVALID_ID_FORMAT);
        }
    }
}
