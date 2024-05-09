package com.hixtrip.sample.domain.order.anno;

import com.hixtrip.sample.domain.order.constant.OrderPayStatusEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PaymentCallback {

    OrderPayStatusEnum value();
}
