package wane.study.springsecuritywithbootver3.service;

import static wane.study.springsecuritywithbootver3.constant.ResponseCode.REQUEST_SUCCESS;
import static wane.study.springsecuritywithbootver3.constant.ResponseCode.USERNAME_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wane.study.springsecuritywithbootver3.domain.Member;
import wane.study.springsecuritywithbootver3.domain.MemberRepository;
import wane.study.springsecuritywithbootver3.dto.BaseResDto;
import wane.study.springsecuritywithbootver3.dto.LoginReqDto;
import wane.study.springsecuritywithbootver3.dto.LoginResDto;
import wane.study.springsecuritywithbootver3.dto.RegisterReqDto;

/**
 * @author: wan2daaa
 */

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    public LoginResDto findMember(LoginReqDto dto) {

        Member findMember = memberRepository.findByMemberId(dto.getMemberId())
            .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND.getMessage()));

        return LoginResDto.of(REQUEST_SUCCESS, findMember);

    }

    public BaseResDto signUp(RegisterReqDto dto) {

        Member createMember = Member.createUser(dto, passwordEncoder);

        validateMemberIsExists(dto);

        Member member = memberRepository.save(createMember);

        return BaseResDto.of(REQUEST_SUCCESS);
    }

    private void validateMemberIsExists(RegisterReqDto dto) {
        memberRepository.findByMemberId(dto.getMemberId())
            .ifPresent(member -> {
                throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
            });
    }
}
