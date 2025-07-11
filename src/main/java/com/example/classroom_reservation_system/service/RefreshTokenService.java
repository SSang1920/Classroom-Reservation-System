package com.example.classroom_reservation_system.service;

import com.example.classroom_reservation_system.entity.RefreshToken;
import com.example.classroom_reservation_system.exception.CustomException;
import com.example.classroom_reservation_system.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.classroom_reservation_system.repository.token.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

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

        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByMemberUuid(memberUuid);

        if(existingTokenOpt.isPresent()){
            //이미 토큰 존재시 새 토큰값과 만료시간 업데이트
            RefreshToken existingToken = existingTokenOpt.get();
            existingToken.updateToken(token,expiryDate);
        }else{
            // 토큰 생성
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .memberUuid(memberUuid)
                    .token(token)
                    .expiryDate(expiryDate)
                    .build();
            refreshTokenRepository.save(newRefreshToken);
        }

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
