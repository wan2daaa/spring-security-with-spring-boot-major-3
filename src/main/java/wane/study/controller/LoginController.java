package wane.study.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wane.study.dto.LoginDto;
import wane.study.dto.TokenDto;
import wane.study.jwt.TokenDuration;
import wane.study.service.LoginService;

@RequiredArgsConstructor
@RestController
public class LoginController {

	private final LoginService loginService;

	@PostMapping("/login")
	public ResponseEntity<TokenDto> login(@RequestBody LoginDto dto, HttpServletRequest request) {

		TokenDto tokens = loginService.login(dto);

		ResponseCookie accessTokenCookie = setAccessTokenCookie(tokens);
		ResponseCookie refreshTokenCookie = setRefreshTokenCookie(tokens);

		return ResponseEntity
				.ok()
				.headers(response -> {
					response.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
					response.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
				})
				.body(tokens);
	}

	private ResponseCookie setAccessTokenCookie(TokenDto tokens) {
		return ResponseCookie.from("accessToken", tokens.accessToken())
				.maxAge(TokenDuration.ACCESS_TOKEN_DURATION.getDurationInSecond())
				.path("/")
				.httpOnly(true)
				.secure(true)
				.build();
	}

	private ResponseCookie setRefreshTokenCookie(TokenDto tokens) {
		return ResponseCookie.from("refreshToken", tokens.refreshToken())
				.maxAge(TokenDuration.REFRESH_TOKEN_DURATION.getDurationInSecond())
				.path("/")
				.httpOnly(true)
				.secure(true)
				.build();
	}
}
