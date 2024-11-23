package likelion.prolink.oAuth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String token);

    Optional<RefreshToken> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}