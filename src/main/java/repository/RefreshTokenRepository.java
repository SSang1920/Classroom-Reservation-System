package repository;

import com.example.classroom_reservation_system.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // memberUuid로 찾기
    Optional<RefreshToken> findByMemberUuid(String memberUuid);

    // token 찾기
    Optional<RefreshToken> findByToken(String token);

    // memberUuid 삭제
    void deleteByMemberUuid(String memberUuid);
}
