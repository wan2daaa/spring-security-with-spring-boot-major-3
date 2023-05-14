package wane.study.springsecuritywithbootver3.dto;

import lombok.RequiredArgsConstructor;
import wane.study.springsecuritywithbootver3.constant.ResponseCode;

/**
 * @author: wan2daaa
 */


@RequiredArgsConstructor
public class BaseResDto {

    private final String code;
    private final String message;

    public static BaseResDto of(String code, String message) {
        return new BaseResDto(code, message);
    }

    public static BaseResDto of(ResponseCode responseCode) {
        return new BaseResDto(responseCode.name(), responseCode.getMessage());
    }

}
