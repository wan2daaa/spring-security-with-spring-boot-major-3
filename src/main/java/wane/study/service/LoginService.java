package wane.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wane.study.dto.LoginDto;
import wane.study.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class LoginService {

	private final UserRepository userRepository;


	public void login(LoginDto dto) {
		userRepository.findByLoginId(dto.loginId());

	}
}
