package com.hixtrip.sample.config;

import com.hixtrip.sample.app.exception.CustomException;
import com.hixtrip.sample.common.ResponseCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class CustomExceptionResolver {
    @ExceptionHandler(value = CustomException.class)
    @ResponseBody
    public ResponseCode exception(CustomException e) {
        return ResponseCode.fail(e.getMessage());
    }
}
