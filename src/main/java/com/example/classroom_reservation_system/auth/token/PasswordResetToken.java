package com.example.classroom_reservation_system.auth.token;

import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token", indexes = {
        @Index(name = "idx_password_reset_token", columnList = "token")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "password_reset_token_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    @Column(name = "used", nullable = false)
    private boolean used = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /**
     * 비즈니스 로직: 토큰 사용 처리
     */
    public void use() {
        if (this.used) {
            throw new CustomException(ErrorCode.PASSWORD_RESET_TOKEN_ALREADY_USED);
        }
        if (isExpired()) {
            throw new CustomException(ErrorCode.PASSWORD_RESET_TOKEN_EXPIRED);
        }

        this.used = true;
    }

    /**
     * 비즈니스 로직: 토큰 만료되었는지 확인
     * @return 만료되었다면 true
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
