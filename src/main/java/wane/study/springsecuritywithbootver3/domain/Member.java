package wane.study.springsecuritywithbootver3.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import wane.study.springsecuritywithbootver3.constant.MemberRole;
import wane.study.springsecuritywithbootver3.constant.MemberSex;
import wane.study.springsecuritywithbootver3.dto.RegisterReqDto;

/**
 * @author: wan2daaa
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String memberId;
    private String password;
    private String name;

    private int age;

    private MemberSex sex;

    private MemberRole role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return getPassword();
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    private Member(
        String memberId,
        String password,
        String name,
        int age,
        MemberSex sex,
        MemberRole role
    ) {
        this.memberId = memberId;
        this.password = password;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.role = role;
    }

    public static Member createUser(
        RegisterReqDto reqDto,
        PasswordEncoder passwordEncoder
    ) {
        return new Member(
            reqDto.getMemberId(),
            passwordEncoder.encode(reqDto.getPassword()),
            reqDto.getName(),
            reqDto.getAge(),
            reqDto.getMemberSex(),
            reqDto.getMemberRole()
        );
    }

}
