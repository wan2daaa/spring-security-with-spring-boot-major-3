package wane.study.springsecuritywithbootver3.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wane.study.springsecuritywithbootver3.constant.ResponseCode;
import wane.study.springsecuritywithbootver3.dto.BaseResDto;

/**
 * @author: wan2daaa
 */
@RestControllerAdvice
public class GeneralExceptionHandler {


    @ExceptionHandler
    public BaseResDto handleUsernameNotFoundException(UsernameNotFoundException e) {
        return BaseResDto.of(ResponseCode.USERNAME_NOT_FOUND);
    }


}
