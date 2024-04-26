package wane.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import wane.study.entity.User;
import wane.study.service.RegisterService;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@RestController
public class RegisterController {

	private final RegisterService registerService;

	private static final AtomicInteger i = new AtomicInteger(1);

	@PostMapping("/register")
	public ResponseEntity<User> register() {
		User user = registerService.registerUser("user" + i.getAndIncrement(), "password");

		return ResponseEntity.ok(user);
	}
}
