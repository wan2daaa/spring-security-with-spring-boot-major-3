package wane.study.springsecuritywithbootver3.config;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author: wan2daaa
 */
public class MemberPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(encode(rawPassword));
    }

}
