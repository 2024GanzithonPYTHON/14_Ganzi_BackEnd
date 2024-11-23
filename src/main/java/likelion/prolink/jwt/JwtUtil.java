package likelion.prolink.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessExpiration;

    public JwtUtil(@Value("${spring.jwt.secret-key}") String secret,
                   @Value("${spring.jwt.access-expiration}") Long accessExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // Use HS256 key
        this.accessExpiration = accessExpiration;
    }

    public String getLoginId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("loginId", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public Boolean isExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token validation error: {}", e.getMessage());
            return true; // Treat as expired if token is invalid
        }
    }

    public String createJwt(String loginId) {
        return Jwts.builder()
                .setSubject(loginId) // Set subject for better clarity
                .claim("loginId", loginId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration)) // Set expiration
                .signWith(secretKey, SignatureAlgorithm.HS256) // Use correct algorithm
                .compact();
    }
}