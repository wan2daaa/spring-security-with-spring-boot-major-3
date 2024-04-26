package wane.study.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenUtils {

	private SecretKey secretKey;

	public JwtTokenUtils(@Value("${jwt.secret-key}") String key) {
		this.secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	}

	public Boolean isTokenValid(String token, Long userId) {
		Long id = Long.valueOf(getId(token));
		return id.equals(userId);
	}


	public Jws<Claims> parseJwtToken(String token) throws ExpiredJwtException {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);
	}

	public String getId(String token) {
		return parseJwtToken(token).getPayload().getSubject();
	}


	public String generateJwtToken(Long id, long expireTime) {
		return Jwts.builder()
				.id(UUID.randomUUID().toString())
				.subject(id.toString())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expireTime))
				.signWith(secretKey)
				.compact();
	}

}
