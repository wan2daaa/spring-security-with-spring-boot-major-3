package wane.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wane.study.entity.User;
import wane.study.service.PrincipalService;

@RequiredArgsConstructor
@RestController
public class AuthorizeController {

	private final PrincipalService principalService;

	@GetMapping("/api/authorize")
	public User authorize(@AuthenticationPrincipal User user) {
		return principalService.changeName(user);
	}

	@GetMapping("/api/admin")
	public String admin() {
		return "admin";
	}
}
