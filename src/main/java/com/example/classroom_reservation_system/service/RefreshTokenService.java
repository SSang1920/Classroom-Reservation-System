package com.example.classroom_reservation_system.service;

import com.example.classroom_reservation_system.entity.RefreshToken;
import com.example.classroom_reservation_system.exception.CustomException;
import com.example.classroom_reservation_system.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.classroom_reservation_system.repository.RefreshTokenRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * RefreshToken 저장(새로 발급할 때)
     */
    public void saveRefreshToken(String memberUuid, String token, long refreshTokenExpirationMillis) {

        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(refreshTokenExpirationMillis / 1000);

        // RefreshToken 생성
        RefreshToken refreshToken = RefreshToken.builder()
                .memberUuid(memberUuid)
                .token(token)
                .expiryDate(expiryDate)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    /**
     * RefreshToken 검증
     */
    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));
    }

    /**
     * memberUuid로 RefreshToken 삭제 (로그아웃)
     */
    public void deleteByMemberUuid(String memberUuid) {
        refreshTokenRepository.deleteByMemberUuid(memberUuid);
    }
}
