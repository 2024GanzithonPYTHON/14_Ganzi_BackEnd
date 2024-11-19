package likelion.prolink.jwt;

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
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("loginId", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // Log the issue and handle invalid tokens
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