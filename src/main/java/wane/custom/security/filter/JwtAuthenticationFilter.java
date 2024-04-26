package wane.custom.security.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import wane.custom.security.authentication.JwtUserAuthenticationToken;

import java.io.IOException;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


	private static final AntPathRequestMatcher ANY_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/**");

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(ANY_ANT_PATH_REQUEST_MATCHER, authenticationManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		JwtUserAuthenticationToken token = new JwtUserAuthenticationToken(authorizationHeader);
		return this.getAuthenticationManager().authenticate(token);
	}


}
