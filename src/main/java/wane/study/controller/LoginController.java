package wane.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wane.study.dto.LoginDto;
import wane.study.service.LoginService;

@RequiredArgsConstructor
@RestController
public class LoginController {

	private final LoginService loginService;

	@PostMapping("/login")
	public String login(@RequestBody LoginDto dto) {

		loginService.login(dto);

		return "login";
	}
}
