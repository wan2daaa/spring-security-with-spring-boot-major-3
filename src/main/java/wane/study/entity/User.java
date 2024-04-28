package wane.study.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "users")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@Column(unique = true, nullable = false)
	private String loginId;

	@Column(nullable = false)
	private String password;

	@Setter
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserAuthority authority;


	public User(String loginId, String password, UserAuthority authority) {
		this.loginId = loginId;
		this.password = password;
		this.authority = authority;
	}

	public static User createUser(String loginId, String password) {
		return new User(loginId, password, UserAuthority.ROLE_USER);
	}

	public static User createAdmin(String loginId, String password) {
		return new User(loginId, password, UserAuthority.ROLE_ADMIN);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(authority.name()));
	}

	@Override
	public String getUsername() {
		return loginId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
