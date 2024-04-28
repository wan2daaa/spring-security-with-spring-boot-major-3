package wane.study.security;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
public class UserAuthenticationProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware {

	private final Log logger = LogFactory.getLog(getClass());

	private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	@Setter
	private UserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Object credentials =  authentication.getCredentials();
		log.info("here");
		if (credentials == null) {
			log.info("here");
			return new UserAuthenticationToken(null);
		}

		try {
			UserDetails findUser = userDetailsService.loadUserByUsername((String) credentials);
			log.info("findUser = {} ", findUser);

			if (findUser == null) {
				throw new InternalAuthenticationServiceException(
						"UserDetailsService returned null, which is an interface contract violation");
			}

			UserAuthenticationToken authenticationToken = new UserAuthenticationToken(findUser, credentials, findUser.getAuthorities());
			authenticationToken.setDetails(findUser);
			return authentication;
		} catch (UsernameNotFoundException e) {
			this.logger.debug("Failed to find user userId='" + credentials + "'");
			throw new BadCredentialsException(this.messages
					.getMessage("UserAuthenticationProvider.badCredentials", e.getMessage()));
		}

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UserAuthenticationToken.class);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
//		Assert.notNull(this.messages, "A message source must be set");
//		Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}
}
