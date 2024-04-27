package wane.study.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class JwtTokenUtilsTest {

	@Autowired
	private JwtTokenUtils jwtTokenUtils;


	@Test
	void make() throws Exception {
		String token = jwtTokenUtils.generateJwtToken(1L, TokenDuration.ACCESS_TOKEN_DURATION.getDuration(), "testToken");

		log.info("token = {} ", token);
	}


	@Test
	void isValidToken() throws Exception {
		// Given
		long userId = 1L;
		String token = jwtTokenUtils.generateJwtToken(userId, TokenDuration.ACCESS_TOKEN_DURATION.getDuration(), "testToken");

		// Then
		// When
		assertThat(jwtTokenUtils.isTokenValid(token, userId)).isTrue();
	}


	@Test
	void isNotValidToken() throws Exception {
		// Given
		long userId = 1L;
		long wrongUserId = 2L;
		String token = jwtTokenUtils.generateJwtToken(userId, TokenDuration.ACCESS_TOKEN_DURATION.getDuration(), "testToken");

		// Then
		// When
		assertThat(jwtTokenUtils.isTokenValid(token, wrongUserId)).isFalse();
	}

	@Test
	void isExpiredToken() throws Exception {
		// Given
		long userId = 1L;
		String token = jwtTokenUtils.generateJwtToken(userId, -1000L, "testToken");

		// Then
		// When
		assertThatThrownBy(() -> jwtTokenUtils.parseJwtToken(token))
				.isInstanceOf(ExpiredJwtException.class);
	}


}