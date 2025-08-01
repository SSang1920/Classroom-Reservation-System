package com.example.classroom_reservation_system.auth.service;

import com.example.classroom_reservation_system.auth.token.RefreshToken;
import com.example.classroom_reservation_system.auth.token.RefreshTokenRepository;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.config.security.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenGenerator tokenGenerator;

    /**
     * RefreshToken 저장, 갱신
     * @param memberUuid 사용자의 UUID
     * @param token 해싱되지 않은 원본 토큰
     * @param refreshTokenExpirationMillis 만료 시간(밀리초)
     */
    public void saveRefreshToken(String memberUuid, String token, long refreshTokenExpirationMillis) {
        // 원본 토큰을 해싱해서 저장
        String hashedToken = tokenGenerator.hashToken(token);
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(refreshTokenExpirationMillis / 1000);

        // 존재하는 토큰 하나 찾기
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByMemberUuid(memberUuid);

        if (existingTokenOpt.isPresent()){
            // 이미 토큰 존재시 새 토큰값과 만료시간 업데이트
            RefreshToken existingToken = existingTokenOpt.get();
            existingToken.updateToken(hashedToken, expiryDate);
        } else {
            // 토큰 생성
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .memberUuid(memberUuid)
                    .token(hashedToken)
                    .expiryDate(expiryDate)
                    .build();

            refreshTokenRepository.save(newRefreshToken);
        }

    }

    /**
     * RefreshToken 검증
     * @param token 해싱되지 않은 원본 토큰
     * @return 유효한 RefreshToken 객체
     */
    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {
        String hashedToken = tokenGenerator.hashToken(token);

        return refreshTokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));
    }

    /**
     * memberUuid로 RefreshToken 삭제 (로그아웃)
     */
    public void deleteByMemberUuid(String memberUuid) {
        refreshTokenRepository.deleteByMemberUuid(memberUuid);
    }
}
