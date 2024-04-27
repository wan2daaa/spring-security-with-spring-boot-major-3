package wane.study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;
import wane.study.jwt.JwtTokenUtils;
import wane.study.repository.UserRepository;
import wane.study.security.JwtAuthenticationFilter;
import wane.study.security.UserAuthenticationProvider;
import wane.study.service.UserDetailService;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import static wane.study.entity.UserRole.ROLE_USER;

@EnableWebSecurity
@Configuration
public class DefaultSecurityConfig {

	@Profile({"default", "local"})
	@Bean
	public SecurityFilterChain testSecurityFilterChain(HttpSecurity http, JwtTokenUtils jwtTokenUtils, UserRepository userRepository) throws Exception {
		return http
				.authenticationManager(authenticationManager(userRepository))
				.authenticationProvider(customAuthenticationProvider(userRepository))
				.authorizeHttpRequests(getHttpRequestsCustom())
				.addFilterAfter(new JwtAuthenticationFilter(antMatcher("/**"), authenticationManager(userRepository), jwtTokenUtils), CorsFilter.class)
				//h2 console 접근 위해 추가
				.csrf(AbstractHttpConfigurer::disable)
				//
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.anonymous(AbstractHttpConfigurer::disable)
				.requestCache(AbstractHttpConfigurer::disable)
				.headers(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				.build();
	}

	@Profile("prod")
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenUtils jwtTokenUtils, UserRepository userRepository) throws Exception {
		http.authorizeHttpRequests(getHttpRequestsCustom())
				.addFilterAfter(new JwtAuthenticationFilter(antMatcher("/**"), authenticationManager(userRepository), jwtTokenUtils), CorsFilter.class);
		return http.build();
	}


	@Bean
	public AuthenticationManager authenticationManager(UserRepository userRepository) {
		return new ProviderManager(customAuthenticationProvider(userRepository));
	}

	@Bean
	public AuthenticationProvider customAuthenticationProvider(UserRepository userRepository) {
		UserAuthenticationProvider provider = new UserAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService(userRepository));
		return provider;
	}


	@Bean
	UserDetailService userDetailsService(UserRepository userRepository) {
		return new UserDetailService(userRepository);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}


	private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> getHttpRequestsCustom() {
		return auth -> {
			auth.requestMatchers(HttpMethod.POST, "/register").permitAll();
			auth.requestMatchers(HttpMethod.POST, "/login").permitAll();
			auth.requestMatchers(antMatcher("/api/authorize")).hasRole(ROLE_USER.getRoleName());
			auth.requestMatchers(antMatcher("/h2/**")).permitAll();
			auth.anyRequest().denyAll();
		};
	}
}
