package wane.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wane.study.dto.LoginDto;
import wane.study.dto.TokenDto;
import wane.study.entity.User;
import wane.study.jwt.JwtTokenUtils;
import wane.study.jwt.TokenDuration;
import wane.study.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class LoginService {

	private final PasswordEncoder passwordEncoder;
	private final JwtTokenUtils jwtTokenUtils;

	private final UserRepository userRepository;


	public TokenDto login(LoginDto dto) {
		User user = userRepository.findByLoginId(dto.loginId())
				.orElseThrow();

		if (passwordEncoder.matches(dto.password(), user.getPassword())) {
			String accessToken = jwtTokenUtils.generateJwtToken(user.getId(), TokenDuration.ACCESS_TOKEN_DURATION.getDuration(), "accessToken");
			String refreshToken = jwtTokenUtils.generateJwtToken(user.getId(), TokenDuration.REFRESH_TOKEN_DURATION.getDuration(), "refreshToken");
			return TokenDto.of(accessToken, refreshToken);
		}

		throw new RuntimeException();

	}
}
