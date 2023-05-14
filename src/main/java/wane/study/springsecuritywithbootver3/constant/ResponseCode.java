package wane.study.springsecuritywithbootver3.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author: wan2daaa
 */

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    REQUEST_SUCCESS("요청 성공"),
    USERNAME_NOT_FOUND("존재하지 않는 아이디입니다.");

    private final String message;

    }
