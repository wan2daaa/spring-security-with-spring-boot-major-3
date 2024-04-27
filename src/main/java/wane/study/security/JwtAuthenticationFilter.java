package wane.study.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import wane.study.jwt.JwtTokenUtils;
import wane.study.jwt.TokenDuration;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private final JwtTokenUtils jwtTokenUtils;

	public JwtAuthenticationFilter(RequestMatcher requestMatcher, AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils) {
		super(requestMatcher, authenticationManager);
		this.jwtTokenUtils = jwtTokenUtils;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

		log.info("super.getAuthenticationManager() = {} ", super.getAuthenticationManager());

		String accessToken = "";
		String refreshToken = "";

		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			return getAuthenticationManager().authenticate(new UserAuthenticationToken(null));
		}
		for (Cookie cookie : cookies) {
			log.info("cookie.getName() = {} ", cookie.getName());
			if (cookie.getName().equals("accessToken")) {
				accessToken = cookie.getValue();
			}
			if (cookie.getName().equals("refreshToken")) {
				refreshToken = cookie.getValue();
			}
		}

		if (
				StringUtils.hasText(accessToken)
//				&& accessToken.startsWith("Bearer ")
		) {
//			String token = accessToken.substring(7); // "Bearer " 다음의 토큰 부분 추출
			try {
				String userId = jwtTokenUtils.getUserId(accessToken);

				if (StringUtils.hasText(userId)) {
					return getAuthenticationManager().authenticate(new UserAuthenticationToken(userId));
				}
			} catch (ExpiredJwtException e) {
				try {
					String userId = jwtTokenUtils.getUserId(refreshToken);

					ResponseCookie cookie = makeNewAccessTokenWithCookie(userId);
					setCookieToResponse(response, cookie);

					return getAuthenticationManager().authenticate(new UserAuthenticationToken(userId));
				} catch (ExpiredJwtException refreshTokenExpiredException) {
					throw new InternalAuthenticationServiceException(e.getMessage(), refreshTokenExpiredException);
				}
			} catch (SignatureException e) {
				throw new InternalAuthenticationServiceException(e.getMessage(), e);
			}
		}
		return getAuthenticationManager().authenticate(new UserAuthenticationToken(null));
	}

	private static void setCookieToResponse(HttpServletResponse response, ResponseCookie cookie) {
		response.setHeader(
				HttpHeaders.SET_COOKIE,
				cookie.toString()
		);
	}

	private ResponseCookie makeNewAccessTokenWithCookie(String userId) {
		String newAccessToken = jwtTokenUtils.generateJwtToken(Long.valueOf(userId), TokenDuration.ACCESS_TOKEN_DURATION.getDuration(), "accessToken");
		return ResponseCookie.from(
						"accessToken",
						newAccessToken
				).path("/")
				.httpOnly(true)
				.secure(true)
				.build();
	}


	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(authResult);

		chain.doFilter(request, response);
	}

}
