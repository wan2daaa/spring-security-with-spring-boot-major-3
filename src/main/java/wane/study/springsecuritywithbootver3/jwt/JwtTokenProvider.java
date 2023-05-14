package wane.study.springsecuritywithbootver3.jwt;

import static io.jsonwebtoken.SignatureAlgorithm.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import wane.study.springsecuritywithbootver3.constant.MemberRole;
import wane.study.springsecuritywithbootver3.dto.TokenDto;

/**
 * @author: wan2daaa
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final String SECRET_KEY = Base64.getEncoder().encodeToString("wan2daaa".getBytes());

    private final long JWT_EXPIRATION_TIME = 1000L * 60 * 60; // 1시간


    /**
     * JWT
     *
     * Claim 클레임
     * 클레임은 Payload 부분에 포함 되어있고,
     * 토큰에서 사용할 데이터들 이라고 생각하면됨!
     *
     * Signature == Sign == (서명)
     * 1. 토큰 인코딩
     * 2. 토큰 유효성 검증
     * 고유한 암호화 코드
     */

    public String getMemberNameFromToken(String token) {
        return getClaimFromToken(token, Claims::getId);
    }

    // 모든 Claim 리턴
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}


    public TokenDto generateAccessTokenAndRefreshToken(String memberId) {

        String accessToken = generateAccessTokenWithMemberId(memberId);
        String refreshToken = generateRefreshTokenWithMemberId(memberId);

        return TokenDto.builder()
            .grantType("Bearer")
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
    public String generateAccessTokenWithMemberId(String memberId) {

        Map<String, Object> claims = new ConcurrentHashMap<>();

        return Jwts.builder()
            .claim("role", MemberRole.MEMBER)
            .setId(memberId)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME * 1))
            .signWith(HS256, SECRET_KEY)
            .compact();
    }

    public String generateRefreshTokenWithMemberId(String memberId) {
        return Jwts.builder()
            .setId(memberId)
            .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME * 5))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(HS256, SECRET_KEY)
            .compact();
    }

    // JWT refreshToken 만료체크 후 재발급
    public Boolean reGenerateRefreshToken(String id) throws Exception {
        // 관리자 정보 조회
        AdminDTO aDTO = new AdminDTO();
        // DB에서 정보 조회
        // ...

        // DB에서 refreshToken 정보 조회
        RefreshTokenDTO rDTO = new RefreshTokenDTO();
        // ... DB 조회 부분

        // refreshToken 정보가 존재하지 않는 경우
        if(rDTO == null) {
            log.info("[reGenerateRefreshToken] refreshToken 정보가 존재하지 않습니다.");
            return false;
        }

        // refreshToken 만료 여부 체크
        try {
            String refreshToken = rDTO.getRefreshToken().substring(7);
            Jwts.parser().setSigningKey(secret).parseClaimsJws(refreshToken);
            log.info("[reGenerateRefreshToken] refreshToken이 만료되지 않았습니다.");
            return true;
        }
        // refreshToken이 만료된 경우 재발급
        catch(ExpiredJwtException e) {
            rDTO.setRefreshToken("Bearer " + generateRefreshTokenWithMemberId(id));
            // ... DB에서 refreshToken 정보 수정
            log.info("[reGenerateRefreshToken] refreshToken 재발급 완료 : {}", "Bearer " + generateRefreshTokenWithMemberId(id));
            return true;
        }
        // 그 외 예외처리
        catch(Exception e) {
            log.error("[reGenerateRefreshToken] refreshToken 재발급 중 문제 발생 : {}", e.getMessage());
            return false;
        }
    }


    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Boolean validateToken(String token ) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
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

        return false;
    }

}
