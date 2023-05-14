package wane.study.springsecuritywithbootver3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wane.study.springsecuritywithbootver3.constant.MemberRole;
import wane.study.springsecuritywithbootver3.constant.ResponseCode;
import wane.study.springsecuritywithbootver3.domain.Member;

/**
 * @author: wan2daaa
 */
@Getter
public final class LoginResDto extends BaseResDto{


    private final String memberId;

    private final String name;

    private final MemberRole memberRole;

    private LoginResDto(String code, String message, String memberId, String name,
        MemberRole memberRole) {
        super(code, message);
        this.memberId = memberId;
        this.name = name;
        this.memberRole = memberRole;
    }

    public static LoginResDto of(ResponseCode responseCode, Member findMember) {
        return new LoginResDto(
            responseCode.name(),
            responseCode.getMessage(),
            findMember.getMemberId(),
            findMember.getName(),
            findMember.getRole()
        );
    }

}
