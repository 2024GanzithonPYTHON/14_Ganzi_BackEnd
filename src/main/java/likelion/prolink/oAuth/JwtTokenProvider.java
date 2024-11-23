package likelion.prolink.oAuth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jdk.jfr.Category;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.security.Key;
import java.util.Date;


@Component
public class JwtTokenProvider {

	private final Key key;

	public JwtTokenProvider(@Value("${spring.jwt.secret-key}") String secretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public String generate(String subject, Date expiredAt) {
		return Jwts.builder()
				.setSubject(subject)
				.setExpiration(expiredAt)
				.signWith(key, SignatureAlgorithm.HS512)
				.compact();
	}

	public String extractSubject(String accessToken) {
		Claims claims = parseClaims(accessToken);
		return claims.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			// Create JwtParser and validate the token
			JwtParser jwtParser = Jwts.parserBuilder()
					.setSigningKey(key)
					.build();
			jwtParser.parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException e) {
			throw new ExpiredJwtException(null, e.getClaims(), "Token expired", e);
		} catch (UnsupportedJwtException e) {
			throw new UnsupportedJwtException("Unsupported JWT: " + e.getMessage(), e);
		} catch (MalformedJwtException e) {
			throw new MalformedJwtException("Malformed JWT: " + e.getMessage(), e);
		} catch (SignatureException e) {
			throw new SignatureException("Invalid JWT signature: " + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Illegal argument: " + e.getMessage(), e);
		}
	}

	private Claims parseClaims(String accessToken) {
		try {
			// Create JwtParser and parse claims
			JwtParser jwtParser = Jwts.parserBuilder()
					.setSigningKey(key)
					.build();
			return jwtParser.parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	public boolean isTokenExpired(String token) {
		try {
			Claims claims = parseClaims(token);
			return claims.getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		}
	}
}

