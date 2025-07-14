package com.example.classroom_reservation_system.auth.service;

import com.example.classroom_reservation_system.auth.dto.request.FindPasswordRequest;
import com.example.classroom_reservation_system.auth.dto.request.ResetPasswordRequest;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.auth.token.PasswordResetToken;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.auth.token.PasswordResetTokenRepository;
import com.example.classroom_reservation_system.config.security.TokenGenerator;
import com.example.classroom_reservation_system.common.mail.MailService;
import com.example.classroom_reservation_system.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final MemberService memberService;
    private final MailService mailService;
    private final TokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    /**
     * 비밀번호 재설정 토큰 생성하고 사용자에게 메일 발송
     */
    public void createAndSendResetToken(FindPasswordRequest request) {
        // 사용자 찾기
        Member member = memberService.findMemberById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String originalToken = tokenGenerator.generateSecureUrlToken(32);
        String hashedToken = tokenGenerator.hashToken(originalToken);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken entity = PasswordResetToken.builder()
                .token(hashedToken)
                .expiresAt(expiresAt)
                .used(false)
                .member(member)
                .build();
        tokenRepository.save(entity);

        // 사용자에게 원본 토큰이 포함된 링크 전송
        String link = UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/reset-password")
                .queryParam("token", originalToken)
                .toUriString();

        mailService.sendResetPasswordMail(member.getEmail(), link);
    }

    /**
     * 비밀번호 재설정
     */
    public void resetPassword(ResetPasswordRequest request) {
        // 비밀번호 일치 여부 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }

        // 토큰 유효성 검사
        String hashedToken = tokenGenerator.hashToken(request.getToken());
        PasswordResetToken resetToken = tokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESET_TOKEN));

        // 토큰 사용 처리
        resetToken.use();

        Member member = resetToken.getMember();

        // 이전 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(request.getNewPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.CANNOT_USE_OLD_PASSWORD);
        }

        member.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }
}
