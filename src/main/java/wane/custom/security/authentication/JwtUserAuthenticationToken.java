package wane.custom.security.authentication;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.Assert;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JwtUserAuthenticationToken implements Authentication, CredentialsContainer {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final Collection<GrantedAuthority> authorities;

	@Getter @Setter
	private String token;

	private Object details;

	@Setter
	private Object principle;

	private boolean authenticated = false;

	public JwtUserAuthenticationToken(String token) {
		this(Collections.emptyList());
		Assert.hasText(token, "token cannot be empty");
		this.token = token;
	}

	public JwtUserAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
		if (authorities == null) {
			this.authorities = AuthorityUtils.NO_AUTHORITIES;
			return;
		}
		for (GrantedAuthority a : authorities) {
			Assert.notNull(a, "Authorities collection cannot contain any null elements");
		}
		this.authorities = List.copyOf(authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public Object getPrincipal() {
		return principle;
	}



	public String getName() {
		if (this.getPrincipal() instanceof Principal principal) {
			return principal.getName();
		}
		return (this.getPrincipal() == null) ? "" : this.getPrincipal().toString();
	}

	@Override
	public boolean implies(Subject subject) {
		return Authentication.super.implies(subject);
	}

	@Override
	public boolean isAuthenticated() {
		return this.authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) {
		this.authenticated = authenticated;
	}

	@Override
	public Object getDetails() {
		return this.details;
	}



	public void setDetails(Object details) {
		this.details = details;
	}

	@Override
	public void eraseCredentials() {
		eraseSecret(getCredentials());
		eraseSecret(getPrincipal());
		eraseSecret(this.details);
	}

	private void eraseSecret(Object secret) {
		if (secret instanceof CredentialsContainer) {
			((CredentialsContainer) secret).eraseCredentials();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractAuthenticationToken test)) {
			return false;
		}
		if (!this.authorities.equals(test.getAuthorities())) {
			return false;
		}
		if ((this.details == null) && (test.getDetails() != null)) {
			return false;
		}
		if ((this.details != null) && (test.getDetails() == null)) {
			return false;
		}
		if ((this.details != null) && (!this.details.equals(test.getDetails()))) {
			return false;
		}
		if ((this.getCredentials() == null) && (test.getCredentials() != null)) {
			return false;
		}
		if ((this.getCredentials() != null) && !this.getCredentials().equals(test.getCredentials())) {
			return false;
		}
		if (this.getPrincipal() == null && test.getPrincipal() != null) {
			return false;
		}
		if (this.getPrincipal() != null && !this.getPrincipal().equals(test.getPrincipal())) {
			return false;
		}
		return this.isAuthenticated() == test.isAuthenticated();
	}

	@Override
	public int hashCode() {
		int code = 31;
		for (GrantedAuthority authority : this.authorities) {
			code ^= authority.hashCode();
		}
		if (this.getPrincipal() != null) {
			code ^= this.getPrincipal().hashCode();
		}
		if (this.getCredentials() != null) {
			code ^= this.getCredentials().hashCode();
		}
		if (this.getDetails() != null) {
			code ^= this.getDetails().hashCode();
		}
		if (this.isAuthenticated()) {
			code ^= -37;
		}
		return code;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append(" [");
		sb.append("Principal=").append(getPrincipal()).append(", ");
		sb.append("Credentials=[PROTECTED], ");
		sb.append("Authenticated=").append(isAuthenticated()).append(", ");
		sb.append("Details=").append(getDetails()).append(", ");
		sb.append("Granted Authorities=").append(this.authorities);
		sb.append("]");
		return sb.toString();
	}
}
