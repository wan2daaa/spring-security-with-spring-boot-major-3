package wane.study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import static wane.study.entity.UserRole.ROLE_USER;

@EnableWebSecurity
@Configuration
public class DefaultSecurityConfig {

	/**
	 * DisableEncodeUrlFilter
	 * WebAsyncManagerIntegrationFilter
	 * SecurityContextHolderFilter
	 * HeaderWriterFilter
	 * CorsFilter
	 * LogoutFilter
	 * RequestCacheAwareFilter
	 * SecurityContextHolderAwareRequestFilter
	 * AnonymousAuthenticationFilter
	 *
	 * ExceptionTranslationFilter
	 * AuthorizationFilter
	 */

	@Bean
	@Profile({"default", "local"})
	public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
		return http
//				.authenticationProvider(AuthenticationProvider)
				.authorizeHttpRequests(getHttpRequestsCustom())
				//h2 console 접근 위해 아래 두개 추가
				.csrf(AbstractHttpConfigurer::disable)
				//
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.anonymous(AbstractHttpConfigurer::disable)
				.requestCache(cache -> cache.requestCache(null))
				.headers(AbstractHttpConfigurer::disable)
//				.logout()
				.build();

	}


	@Bean
	@Profile("prod")
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(getHttpRequestsCustom());
		return http.build();
	}

//	//이 기능은 AuthenticationManagerBuilder에 입력되지 않았고 AuthenticationProviderBean이 정의되지 않은 경우에만 사용됩니다.
//	@Bean
//	UserDetailsSource userDetailsService(UserRepository userRepository) {
//		return new UserDetailsSource(userRepository);
//	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}





	// Provider Manager 에 여러 개의 Authentication Provider 인스턴스를 주입할 수 있습니다
	// ProviderManager는 AuthenticationManager의 가장 일반적으로 사용되는 구현입니다
//	@Bean
//	public AuthenticationProvider authenticationProvider(UserRepository userRepository) {
//		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//		authProvider.setUserDetailsService(userDetailsService(userRepository));
//
//		// passwordEncoder()가 없어도 Provider 기본 생성자에서 주입 해줌
//
//		return authProvider;
//	}
//


	private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> getHttpRequestsCustom() {
		return auth -> {
			auth.requestMatchers(HttpMethod.POST , "/register").permitAll();
			auth.requestMatchers(HttpMethod.POST, "/login").permitAll();
			auth.requestMatchers(antMatcher("/authorize")).hasRole(ROLE_USER.getRoleName());
			auth.requestMatchers(antMatcher("/h2-console")).permitAll();
			auth.anyRequest().denyAll();
		};
	}
}
