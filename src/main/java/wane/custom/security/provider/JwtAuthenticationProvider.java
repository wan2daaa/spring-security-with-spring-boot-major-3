package wane.custom.security.provider;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.util.Assert;
import wane.study.jwt.JwtTokenUtils;
import wane.custom.security.authentication.JwtUserAuthenticationToken;

public class JwtAuthenticationProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware {

	protected final Log logger = LogFactory.getLog(getClass());

	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	private UserCache userCache = new NullUserCache();

	private boolean hideUserNotFoundExceptions = true;

	private boolean forcePrincipalAsString = false;

	private final JwtTokenUtils jwtTokenUtils;

	private UserDetailsService userDetailsService;

	private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();

	private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();

	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();


	public JwtAuthenticationProvider(JwtTokenUtils jwtTokenUtils) {
		Assert.notNull(jwtTokenUtils, "jwtTokenUtils cannot be null");
		this.jwtTokenUtils = jwtTokenUtils;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.isInstanceOf(JwtUserAuthenticationToken.class, authentication,
				() -> this.messages.getMessage("JwtAuthenticationProvider.authenticate",
						"Only UserAuthentication is supported"));

		JwtUserAuthenticationToken tokenAuthentication = (JwtUserAuthenticationToken) authentication;

		String userId = getUserId(tokenAuthentication);

		boolean cacheWasUsed = true;
		UserDetails user = this.userCache.getUserFromCache(userId);
		if (user == null) { //cache 에 존재하지 않으면,
			cacheWasUsed = false;
			try {
				user = retrieveUser(userId, tokenAuthentication);
				tokenAuthentication.setPrinciple(user);
				tokenAuthentication.setAuthenticated(true);

			} catch (UsernameNotFoundException exception) {
				this.logger.debug("Failed to find user '" + userId + "'");
				if (!this.hideUserNotFoundExceptions) {
					throw exception;
				}
				throw new BadCredentialsException(this.messages
						.getMessage("JwtAuthenticationProvider.badCredentials", "Bad credentials"));
			}
			Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
		}

		try {
			this.preAuthenticationChecks.check(user);
		} catch (AuthenticationException ex) {
			if (!cacheWasUsed) {
				throw ex;
			}
			cacheWasUsed = false;
			user = retrieveUser(userId, tokenAuthentication);
			tokenAuthentication.setPrinciple(user);
			tokenAuthentication.setAuthenticated(true);
			this.preAuthenticationChecks.check(user);
		}

		this.postAuthenticationChecks.check(user);
		if (!cacheWasUsed) {
			this.userCache.putUserInCache(user);
		}
		Object principalToReturn = user;
		if (this.forcePrincipalAsString) {
			principalToReturn = user.getUsername();
		}
		return createSuccessAuthentication(principalToReturn, tokenAuthentication, user);
	}

	private String getUserId(JwtUserAuthenticationToken authentication) {

		if (!authentication.getToken().contains("Bearer ")) {
			throw new AuthenticationException("token must be starts with Bearer") {
				@Override
				public String getMessage() {
					return super.getMessage();
				}
			};
		}

		String accessToken = removeBearer(authentication.getToken());

		return jwtTokenUtils.getId(accessToken);
	}

	private String removeBearer(String BearerToken) {
		return BearerToken.replace("Bearer ", "");
	}


	private UserDetails retrieveUser(String username, JwtUserAuthenticationToken authentication) {
//		prepareTimingAttackProtection();
		try {
			UserDetails loadedUser = this.userDetailsService.loadUserByUsername(username);
			if (loadedUser == null) {
				throw new InternalAuthenticationServiceException(
						"UserDetailsService returned null, which is an interface contract violation");
			}
			return loadedUser;
		} catch (UsernameNotFoundException ex) {
//			mitigateAgainstTimingAttack(authentication);
			throw ex;
		} catch (InternalAuthenticationServiceException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
		}
	}


	private Authentication createSuccessAuthentication(Object principal, Authentication authentication,
													   UserDetails user) {
		// Ensure we return the original credentials the user supplied,
		// so subsequent attempts are successful even with encoded passwords.
		// Also ensure we return the original getDetails(), so that future
		// authentication events after cache expiry contain the details
		this.logger.debug("Authenticated user");
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return JwtUserAuthenticationToken.class.isAssignableFrom(authentication);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.userCache, "A user cache must be set");
		Assert.notNull(this.messages, "A message source must be set");
		Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}

	private class DefaultPreAuthenticationChecks implements UserDetailsChecker {

		@Override
		public void check(UserDetails user) {
			if (!user.isAccountNonLocked()) {
				JwtAuthenticationProvider.this.logger
						.debug("Failed to authenticate since user account is locked");
				throw new LockedException(JwtAuthenticationProvider.this.messages
						.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
			}
			if (!user.isEnabled()) {
				JwtAuthenticationProvider.this.logger
						.debug("Failed to authenticate since user account is disabled");
				throw new DisabledException(JwtAuthenticationProvider.this.messages
						.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
			}
			if (!user.isAccountNonExpired()) {
				JwtAuthenticationProvider.this.logger
						.debug("Failed to authenticate since user account has expired");
				throw new AccountExpiredException(JwtAuthenticationProvider.this.messages
						.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
			}
		}

	}

	private class DefaultPostAuthenticationChecks implements UserDetailsChecker {

		@Override
		public void check(UserDetails user) {
			if (!user.isCredentialsNonExpired()) {
				JwtAuthenticationProvider.this.logger
						.debug("Failed to authenticate since user account credentials have expired");
				throw new CredentialsExpiredException(JwtAuthenticationProvider.this.messages
						.getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired",
								"User credentials have expired"));
			}
		}

	}


}
