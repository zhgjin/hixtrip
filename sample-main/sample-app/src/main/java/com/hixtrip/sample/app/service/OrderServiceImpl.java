package com.hixtrip.sample.app.service;

import com.hixtrip.sample.app.api.OrderService;
import com.hixtrip.sample.app.exception.CustomException;
import com.hixtrip.sample.client.order.dto.CommandOderCreateDTO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.domain.commodity.CommodityDomainService;
import com.hixtrip.sample.domain.inventory.InventoryDomainService;
import com.hixtrip.sample.domain.order.OrderDomainService;
import com.hixtrip.sample.domain.order.anno.PaymentCallback;
import com.hixtrip.sample.domain.order.constant.OrderPayStatusEnum;
import com.hixtrip.sample.domain.order.model.Order;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * app层负责处理request请求，调用领域服务
 */
@Component
public class OrderServiceImpl implements OrderService, InitializingBean {
    @Autowired
    private OrderDomainService orderDomainService;
    @Autowired
    private CommodityDomainService commodityDomainService;
    @Autowired
    private InventoryDomainService inventoryDomainService;
    private Map<String, Function<String,Void>> paymentCallbackHandler = new HashMap<>();

    /**
     * 订单创建
     * @param oderCreateDTO
     * @return
     */
    @Override
    public String orderCreate(CommandOderCreateDTO oderCreateDTO) {
        //扣减库存
        boolean changeInventory = inventoryDomainService.changeInventory(oderCreateDTO.getSkuId(),
                -oderCreateDTO.getAmount().longValue(), oderCreateDTO.getAmount().longValue(), 0L);
        if(!changeInventory){
            throw new CustomException("库存不足");
        }
        //创建订单
        return orderDomainService.createOrder(oderCreateDTO.getSkuId(),"根据skuId获取商家Id", oderCreateDTO.getAmount(),
                commodityDomainService.getSkuPrice(oderCreateDTO.getSkuId()), oderCreateDTO.getUserId());
    }

    @Override
    public void payCallback(CommandPayDTO commandPayDTO) {
        //获取对应状态的回调函数
        Function<String, Void> paymentCallback = paymentCallbackHandler.get(commandPayDTO.getPayStatus());
        if(Objects.isNull(paymentCallback)){
            throw new CustomException("未知的支付状态");
        }
        paymentCallback.apply(commandPayDTO.getOrderId());
    }

    /**
     * 支付成功回调方法
     * @param orderId
     */
    @PaymentCallback(OrderPayStatusEnum.PAID)
    private void orderPaySuccess(String orderId){
        Order order = orderDomainService.getOrderById(orderId);
        orderDomainService.orderPaySuccess(order);
        //变更库存，预占库存转为占用库存
        inventoryDomainService.changeInventory(order.getSkuId(),
                0L, -order.getAmount().longValue(), order.getAmount().longValue());
    }

    /**
     * 支付失败回调方法
     * @param orderId
     */
    @PaymentCallback(OrderPayStatusEnum.FAIL)
    private void orderPayFail(String orderId){
        Order order = orderDomainService.getOrderById(orderId);
        orderDomainService.orderPayFail(order);
        //变更库存，预占库存转为可售库存
        inventoryDomainService.changeInventory(order.getSkuId(),
                order.getAmount().longValue(), -order.getAmount().longValue(), 0L);
    }

    /**
     * 支付回调方案注册
     */
    @Override
    public void afterPropertiesSet() {
        Method[] declaredMethods = this.getClass().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            PaymentCallback annotation = declaredMethod.getAnnotation(PaymentCallback.class);
            if(Objects.isNull(annotation)){
                continue;
            }
            paymentCallbackHandler.put(annotation.value().getStatus(),(orderId)->{
                declaredMethod.setAccessible(true);
                //从待支付列表中移除
                boolean consumed = orderDomainService.consumeOrder(orderId);
                if(!consumed){
                    throw new CustomException("订单重复消费");
                }
                try {
                    declaredMethod.invoke(this,orderId);
                } catch (Exception e) {
                    throw new CustomException("支付回调处理失败");
                }
                return null;
            });
        }
    }
}
