package wane.study.common;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import wane.study.entity.User;
import wane.study.security.UserAuthenticationToken;

import java.util.Collections;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {
	@Override
	public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
		String userId = annotation.userId();

		UserAuthenticationToken authenticationToken = new UserAuthenticationToken(
				new User(annotation.loginId(), annotation.password(), annotation.authority()),
				userId,
				Collections.singleton(new SimpleGrantedAuthority(annotation.authority().name()))
		);

		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authenticationToken);

		return context;
	}



}
