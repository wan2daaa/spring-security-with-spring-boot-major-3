package wane.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wane.study.entity.User;
import wane.study.repository.UserRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PrincipalService {

	private final UserRepository userRepository;

	@Transactional
	public User changeName(User user) {
		user.setName(UUID.randomUUID().toString().substring(0, 7));
		userRepository.save(user);
		return user;
	}

	public User findById(Long id) {
		return userRepository.findById(id)
				.orElseThrow();
	}

}
