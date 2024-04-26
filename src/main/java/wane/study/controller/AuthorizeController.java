package wane.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthorizeController {

	@GetMapping("/authorize")
	public String authorize() {
		return "authorize";
	}
}
