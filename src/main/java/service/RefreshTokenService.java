package service;

import com.example.classroom_reservation_system.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repository.RefreshTokenRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;

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
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("RefreshToken이 유효하지 않습니다."));
    }

    /**
     * memberUuid로 RefreshToken 삭제 (로그아웃)
     */
    public void deleteByMemberUuid(String memberUuid) {
        refreshTokenRepository.deleteByMemberUuid(memberUuid);
    }
}
