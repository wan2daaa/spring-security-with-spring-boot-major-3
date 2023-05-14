package wane.study.springsecuritywithbootver3.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author: wan2daaa
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    // 인증에서 제외할 url
    private static final List<String> EXCLUDE_URL =
        Collections.unmodifiableList(
            Arrays.asList(
                "/static/**",
                "/favicon.ico"
            ));


    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        // jwt local storage 사용 시 해당 코드를 사용하여 header에서 토큰을 받아오도록 함
        // final String token = request.getHeader("Authorization");

        /**
         * 1. request.getCookies() 메소드를 호출하여 현재 요청의 모든 쿠키를 가져옵니다.
         * 2. Arrays.stream(...)을 사용하여 쿠키 배열을 스트림으로 변환합니다.
         * 3. filter(cookie -> cookie.getName().equals("accessToken")) 메소드를 사용하여 "accessToken"이라는 이름을 가진 쿠키를 찾습니다.
         * 4. .findFirst() 메소드를 사용하여 첫 번째로 일치하는 쿠키를 찾습니다. (일치하는 쿠키가 없을 경우 빈 값이 반환됩니다.)
         * 5. map(Cookie::getValue) 메소드를 사용하여 해당 쿠키의 값을 가져옵니다.
         * 6. .orElse(null) 메소드를 사용하여 값이 존재하지 않을 경우 null을 반환합니다.
         * 결과적으로, String token 변수는 "accessToken"이라는 이름을 가진 쿠키의 값을 가지거나, 일치하는 쿠키가 없을 경우 null을 가지게 됩니다.
         */
        // jwt cookie 사용 시 해당 코드를 사용하여 쿠키에서 토큰을 받아오도록 함
        String tokenCookieValue = Arrays.stream(request.getCookies())
            .filter(cookie -> cookie.getName().equals("accessToken"))
            .findFirst().map(Cookie::getValue)
            .orElse(null);

        String memberId = null;
        String jwtToken = null;

        // Bearer token인 경우 JWT 토큰 유효성 검사 진행
        if (tokenCookieValue != null && tokenCookieValue.startsWith("Bearer ")) {
            jwtToken = getJwtTokenWithoutPrefix(tokenCookieValue);
            try {
                memberId = jwtTokenProvider.getUsernameFromToken(jwtToken);
            } catch (SignatureException e) {
                log.error("Invalid JWT signature: {}", e.getMessage());
            } catch (MalformedJwtException e) {
                log.error("Invalid JWT token: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                log.error("JWT token is expired: {}", e.getMessage());
            } catch (UnsupportedJwtException e) {
                log.error("JWT token is unsupported: {}", e.getMessage());
            } catch (IllegalArgumentException e) {
                log.error("JWT claims string is empty: {}", e.getMessage());
            }
        } else {
            logger.error("JWT Token does not begin with Bearer String");
        }

        // token 검증이 되고, 인증 정보가 존재하지 않는 경우 -> spring security 인증 정보 저장
        if(memberId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            AdminDTO adminDTO = new AdminDTO();
            // DB에서 관련 정보 조회
            // ...

            if(jwtTokenProvider.validateToken(jwtToken, adminDTO)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(adminDTO, null ,adminDTO.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // accessToken 인증이 되었다면 refreshToken 재발급이 필요한 경우 재발급
        try {
            if(memberId != null) {
                jwtTokenProvider.reGenerateRefreshToken(memberId);
            }
        }catch (Exception e) {
            log.error("[JwtRequestFilter] refreshToken 재발급 체크 중 문제 발생 : {}", e.getMessage());
        }

        filterChain.doFilter(request,response);


    }

    private String getJwtTokenWithoutPrefix(String tokenCookieValue) {
        return tokenCookieValue.substring(7);
    }
}
