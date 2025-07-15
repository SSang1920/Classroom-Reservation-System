package com.example.classroom_reservation_system.common.mail;

import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MailService {

    private final JavaMailSender mailSender;

    /**
     * HTML 메일 발송
     *  비동기 실행하여 사용자가 바로확인가능하게 함
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendResetPasswordMail(String to, String link) {
        String subject = "[실시간 강의실 예약 시스템] 비밀번호 재설정 안내";
        String content = ""
                + "<h3>비밀번호 재설정 안내</h3>"
                + "<p>아래 링크를 클릭하여 비밀번호를 재설정하세요.</p>"
                + "<p style=\"margin: 25px 0;\">"
                + "<a href=\"" + link + "\" "
                + "style=\"padding:10px 18px; background:#4361ee; color:white; border-radius:6px; text-decoration:none;\">비밀번호 재설정</a>"
                + "</p>"
                + "<div style=\"font-size:0.9em; color:#888;\">"
                + "이 링크는 30분간 유효합니다.<br/>만약 본인이 요청하지 않았다면 이메일을 무시하세요.</div>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true: HTML
            mailSender.send(message);
        }
        catch (MailException | MessagingException e)
        {
            log.error("Mail send error: {}", e.getMessage());
            throw new CustomException(ErrorCode.MAIL_SEND_FAIL);
        }

    }

}
