package wane.study.springsecuritywithbootver3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import wane.study.springsecuritywithbootver3.dto.BaseResDto;
import wane.study.springsecuritywithbootver3.dto.LoginReqDto;
import wane.study.springsecuritywithbootver3.dto.LoginResDto;
import wane.study.springsecuritywithbootver3.dto.RegisterReqDto;
import wane.study.springsecuritywithbootver3.service.MemberService;

/**
 * @author: wan2daaa
 */
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/login")
    public LoginResDto login(LoginReqDto dto) {

        return memberService.findMember(dto);
    }

    @PostMapping("/api/sign-up")
    public BaseResDto signUp(RegisterReqDto dto) {

        return memberService.signUp(dto);
    }

}
