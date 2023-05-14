package wane.study.springsecuritywithbootver3.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wane.study.springsecuritywithbootver3.constant.MemberRole;
import wane.study.springsecuritywithbootver3.constant.MemberSex;

/**
 * @author: wan2daaa
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterReqDto {

    private String memberId;

    private String password;

    private String name;

    private int age;

    private MemberSex memberSex;

    private MemberRole memberRole;


}
