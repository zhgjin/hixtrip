package com.hixtrip.sample.domain.order.constant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderPayStatusEnum {
    PENDING("1","待支付"),
    PAID("2","已付款"),
    FAIL("3","支付失败"),
    CANCEL("4","取消");
    private String status;
    private String label;
}
