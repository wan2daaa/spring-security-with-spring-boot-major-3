package wane.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wane.study.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByLoginId(String username);
}
