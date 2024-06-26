package wane.study.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserAuthority {
	ROLE_USER("USER"), ROLE_ADMIN("ADMIN"),;

	private final String roleName;
}
