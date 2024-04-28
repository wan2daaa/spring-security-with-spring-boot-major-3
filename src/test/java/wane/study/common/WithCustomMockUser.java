package wane.study.common;

import org.springframework.security.test.context.support.WithSecurityContext;
import wane.study.entity.UserAuthority;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {

	String userId() default "1";
	String loginId() default "user_1";
	String password() default "password";
	UserAuthority authority() default UserAuthority.ROLE_USER;
}
