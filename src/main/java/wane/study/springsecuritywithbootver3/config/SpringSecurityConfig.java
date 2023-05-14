package wane.study.springsecuritywithbootver3.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import wane.study.springsecuritywithbootver3.constant.MemberRole;
import wane.study.springsecuritywithbootver3.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {


    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    PasswordEncoder passwordEncoder() {
//        return new MemberPasswordEncoder();
        return new BCryptPasswordEncoder();
    }




    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()  // csrf 비활성화
            .cors().disable()  // cors 비활성화
            .authorizeHttpRequests()
                .requestMatchers("/login", "/sign-in").permitAll()  // 루트 페이지는 모두 접근 가능
                .requestMatchers("/user/**").hasRole(MemberRole.MEMBER.name())
                .requestMatchers("/admin/**").hasRole(MemberRole.ADMIN.name())
                .anyRequest().authenticated()  // 그 외의 페이지는 인증된 사용자만 접근 가능
            .and()
            .exceptionHandling()
                .authenticationEntryPoint((request, response, Exception) -> {
                    response.sendRedirect("/authentication/denied"); // 인증되지 않은 사용자
                })
            .and()
            .formLogin()
                .loginPage("/login")  // 로그인 페이지 url
                .loginProcessingUrl("/api/login")  // 로그인 처리 url
                .usernameParameter("userId")
                .passwordParameter("password")
                .defaultSuccessUrl("/main")  // 로그인 성공 시 이동할 url
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/main")  // 로그아웃 url
                .logoutSuccessUrl("/login")  // 로그아웃 성공 시 이동할 url
                .invalidateHttpSession(true)  // 로그아웃 시 세션 무효화
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 세션 생성 정책 -> 세션 생성 및 사용 X
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

}