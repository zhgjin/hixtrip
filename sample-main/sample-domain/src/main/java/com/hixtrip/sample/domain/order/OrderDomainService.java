package com.hixtrip.sample.domain.order;

import com.hixtrip.sample.domain.order.anno.PaymentCallback;
import com.hixtrip.sample.domain.order.constant.OrderCacheKey;
import com.hixtrip.sample.domain.order.constant.OrderPayStatusEnum;
import com.hixtrip.sample.domain.order.exception.OrderException;
import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.order.repository.OrderRepository;
import com.hixtrip.sample.domain.pay.model.CommandPay;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 订单领域服务
 * todo 只需要实现创建订单即可
 */
@Component
public class OrderDomainService{

    @Autowired
    private OrderRepository orderRepository;

    /**
     * todo 需要实现
     * 创建待付款订单
     */
    public String createOrder(String skuId,String sellerId, Integer amunt, BigDecimal money, String userId) {
        Order order = Order.builder()
                .skuId(skuId)
                .userId(userId)
                .sellerId(sellerId)
                .amount(amunt)
                .money(money)
                .payStatus(OrderPayStatusEnum.PENDING.getStatus())
                .createBy(userId)
                .updateBy(userId)
                .build();
        String orderId = orderRepository.save(order);
        orderRepository.addToPendingList(orderId);
        return orderId;
        //需要你在infra实现, 自行定义出入参
    }

    /**
     * todo 需要实现
     * 待付款订单支付成功
     */
    @PaymentCallback(OrderPayStatusEnum.PAID)
    public void orderPaySuccess(Order order) {
        order.paySuccess();
        orderRepository.update(order);
        //需要你在infra实现, 自行定义出入参
    }

    /**
     * todo 需要实现
     * 待付款订单支付失败
     */
    public void orderPayFail(Order order) {
        order.payFail();
        orderRepository.update(order);
        //需要你在infra实现, 自行定义出入参
    }

    public boolean consumeOrder(String orderId){
        return orderRepository.consumeOrder(orderId);
    }

    public List<Order> select(Order order,int currPage,int pageSize){
        List<String> orderIds = orderRepository.selectIdsPage(order, currPage, pageSize);
        return orderIds.stream().map(this::getOrderById).collect(Collectors.toList());
    }

    @Cacheable(cacheNames = OrderCacheKey.ORDER_DETAIL_KEY,key = "#orderId")
    public Order getOrderById(String orderId){
        return orderRepository.getOrderById(orderId);
    }
}
