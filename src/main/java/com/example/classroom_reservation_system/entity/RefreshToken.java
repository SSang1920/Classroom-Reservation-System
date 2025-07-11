package com.example.classroom_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Refresh_token")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(name = "member_uuid", nullable = false, unique = true)
    private String memberUuid;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    /**
     * 토큰 값과 만료 날짜를 갱신
     */
    public void updateToken(String token, LocalDateTime expiryDate){
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
