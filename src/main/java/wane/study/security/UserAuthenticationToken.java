package wane.study.security;

import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Setter
public class UserAuthenticationToken extends AbstractAuthenticationToken {

	private Object principal;

	private Object credentials; // userId

	public UserAuthenticationToken( Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		setAuthenticated(true);
	}

	public UserAuthenticationToken(Object credentials) {
		super(null);
		this.credentials = credentials;
		setAuthenticated(false);
	}

	public UserAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
		super(null);
		setAuthenticated(true);
	}


	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

}
