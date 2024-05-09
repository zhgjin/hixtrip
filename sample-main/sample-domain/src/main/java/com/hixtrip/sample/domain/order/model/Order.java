package com.hixtrip.sample.domain.order.model;

import com.hixtrip.sample.domain.order.constant.OrderPayStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder(toBuilder = true)
public class Order {

    /**
     * 订单号
     */
    private String id;

    /**
     * 购买人
     */
    private String userId;

    /**
     * 商家ID
     */
    private String sellerId;


    /**
     * SkuId
     */
    private String skuId;

    /**
     * 购买数量
     */
    private Integer amount;

    /**
     * 购买金额
     */
    private BigDecimal money;

    /**
     * 支付状态
     */
    private String payStatus;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改人
     */
    private String updateBy;

    public void paySuccess(){
        this.setPayStatus(OrderPayStatusEnum.PAID.getStatus());
        this.setPayTime(LocalDateTime.now());
    }

    public void payFail(){
        this.setPayStatus(OrderPayStatusEnum.FAIL.getStatus());
    }
}
