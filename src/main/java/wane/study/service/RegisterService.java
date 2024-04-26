package wane.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wane.study.entity.User;
import wane.study.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class RegisterService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public User registerUser(String loginId, String password) {

		return userRepository.save(User.createUser(loginId, passwordEncoder.encode(password)));
	}
}
