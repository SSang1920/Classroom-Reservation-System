package com.example.classroom_reservation_system.service;

import com.example.classroom_reservation_system.exception.CustomException;
import com.example.classroom_reservation_system.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MailService {

    private final JavaMailSender mailSender;

    public void sendResetPasswordMail(String to, String link) {
        String subject = "[실시간 강의실 예약 시스템] 비밀번호 재설정 안내";
        String content = ""
                + "<h3>비밀번호 재설정 안내</h3>"
                + "<p>아래 링크를 클릭하여 비밀번호를 재설정하세요.</br>"
                + "<a href=\"" + link + "\" "
                + "style=\"padding:10px 18px; background:#4361ee; color:white; border-radius:6px; text-decoration:none;\">비밀번호 재설정</a></p>"
                + "<div style=\"font-size:0.9em; color:#888; margin-top:16px;\">"
                + "이 링크는 30분간 유효합니다.<br/>만약 본인이 요청하지 않았다면 이메일을 무시하세요.</div>";

        sendHtmlMail(to, subject, content);
    }

    /**
     * HTML 메일 발송 (공통)
     */
    public void sendHtmlMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true: HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.MAIL_SEND_FAIL);
        }
    }
}
