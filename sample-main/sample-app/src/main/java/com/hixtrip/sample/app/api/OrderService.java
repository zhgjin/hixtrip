package com.hixtrip.sample.app.api;

import com.hixtrip.sample.client.order.dto.CommandOderCreateDTO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;

/**
 * 订单的service层
 */
public interface OrderService {

    String orderCreate(CommandOderCreateDTO oderCreateDTO);

    void payCallback(CommandPayDTO commandPayDTO);
}
