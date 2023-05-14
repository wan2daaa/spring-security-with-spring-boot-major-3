package wane.study.springsecuritywithbootver3.service;

import static wane.study.springsecuritywithbootver3.constant.ResponseCode.*;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import wane.study.springsecuritywithbootver3.domain.Member;
import wane.study.springsecuritywithbootver3.domain.MemberRepository;

/**
 * @author: wan2daaa
 */
@Component
@RequiredArgsConstructor
public class SimpleMemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member findMember = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND.getMessage()));

        return User.builder()
            .username(findMember.getMemberId()) //아이디
            .password(findMember.getPassword()) //비밀번호
            .roles(findMember.getRole().name()) //권한
            .build();
    }
}
