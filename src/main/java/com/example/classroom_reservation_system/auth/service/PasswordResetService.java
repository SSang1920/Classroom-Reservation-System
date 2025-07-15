package com.example.classroom_reservation_system.auth.service;

import com.example.classroom_reservation_system.auth.dto.request.FindPasswordRequest;
import com.example.classroom_reservation_system.auth.dto.request.ResetPasswordRequest;
import com.example.classroom_reservation_system.common.mail.MailService;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.auth.token.PasswordResetToken;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.auth.token.PasswordResetTokenRepository;
import com.example.classroom_reservation_system.config.security.TokenGenerator;

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

    private final MemberService memberService;
    private final PasswordResetTokenRepository tokenRepository;
    private final MailService mailService;
    private final TokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    /**
     * 이메일로 링크 발송
     * sendResetPasswordMail, createAndSavePasswordResetToken 으로 분리하여 트랜잭션 분리
     * @param request 비밀번호를 찾으려는 사용자의 아이디를 담은 요청 객체
     * @throws CustomException 사용자를 찾을 수 없거나, 메일 발송에 실패한 경우 발생
     */
    @Transactional(noRollbackFor = CustomException.class)
    public void sendResetPasswordMail(FindPasswordRequest request){
        String id = request.getId();

        //1. 사용자 조회
        Member member = memberService.findMemberById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        //2. 보안 토큰 생성
        String originalToken = tokenGenerator.generateSecureUrlToken(32);
        String hashedToken = tokenGenerator.hashToken(originalToken);

        //3. 비밀번호 재설정 토큰을 생성하고 DB에 저장 (핵심 DB 트랜잭션)
        createAndSavePasswordResetToken(member, hashedToken);

        //4. 재설정 링크 생성, 이메일 발송
        buildResetLinkAndSendMail(member.getEmail(), originalToken);
    }

    /**
     * 비밀번호 재설정 토큰 생성, DB 저장
     *
     * @param member      토큰을 발급할 대상 회원 객체
     * @param hashedToken 데이터베이스에 저장될 해시된 토큰 값
     */
    private void createAndSavePasswordResetToken(Member member, String hashedToken){
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken entity = PasswordResetToken.builder()
                .token(hashedToken)
                .expiresAt(expiresAt)
                .used(false)
                .member(member)
                .build();

        tokenRepository.save(entity);
    }

    /**
     * 재설정 링크 생성, 이메일 발송
     * @param email 메일을 수신할 사용자의 이메일 주소
     * @param token URL 쿼리 파라미터에 포함될 재설정 토큰
     * @throws CustomException 메일 서버와의 통신 문제 등으로 발송에 실패한 경우 발생
     */
    private void buildResetLinkAndSendMail(String email, String token){
        String link = UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/reset-password")
                .queryParam("token",token)
                .toUriString();
        try{
            mailService.sendResetPasswordMail(email, link);
        } catch (CustomException e){
            throw new CustomException(ErrorCode.MAIL_SEND_FAIL_BUT_TOKEN_SAVED);
        }

    }

    /**
     * 비밀번호 재설정
     * @param request 재설정 토큰, 새 비밀번호, 비밀번호 확인을 담은 요청 객체
     * @throws CustomException 비밀번호와 비밀번호 확인이 일치하지 않거나, 토큰이 유효하지 않거나,
     *                         이전 비밀번호와 동일한 비밀번호로 변경하려 할 경우 발생
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
