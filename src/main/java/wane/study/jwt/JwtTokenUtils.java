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
		Long id = Long.valueOf(getUserId(token));
		return id.equals(userId);
	}


	public Jws<Claims> parseJwtToken(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);
	}

	public String getUserId(String token) throws ExpiredJwtException{
		return parseJwtToken(token).getPayload().get("userId").toString();
	}


	public String generateJwtToken(Long userId, long expireTime, String tokenName) {
		return Jwts.builder()
				.id(UUID.randomUUID().toString())
				.subject(tokenName)
				.claim("userId", userId)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expireTime))
				.signWith(secretKey)
				.compact();
	}

}
