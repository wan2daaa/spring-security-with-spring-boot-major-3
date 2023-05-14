package wane.study.springsecuritywithbootver3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wane.study.springsecuritywithbootver3.domain.Member;
import wane.study.springsecuritywithbootver3.domain.MemberRepository;
import wane.study.springsecuritywithbootver3.dto.RegisterReqDto;

/**
 * @author: wan2daaa
 */
@Service
@RequiredArgsConstructor
public class RegisterMemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public Long join(RegisterReqDto dto) {
        Member member = Member.createUser(dto, passwordEncoder);

        return null;
    }

}
