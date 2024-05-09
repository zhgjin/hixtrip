package com.hixtrip.sample.common;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
public class ResponseCode {
    private String code;
    private Object data;
    private String msg;

    public static ResponseCode success(Object data){
        return ResponseCode.builder().data(data)
                .code("200")
                .build();
    }

    public static ResponseCode fail(String msg){
        return ResponseCode.builder().msg(msg)
                .code("500")
                .build();
    }
}
