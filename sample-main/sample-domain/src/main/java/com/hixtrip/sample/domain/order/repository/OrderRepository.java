package com.hixtrip.sample.domain.order.repository;

import com.hixtrip.sample.domain.order.model.Order;

import java.util.List;

/**
 *
 */
public interface OrderRepository {
    String save(Order order);

    void update(Order order);

    Order getOrderById(String orderId);

    List<String> selectIdsPage(Order order,int currPage,int pageSize);

    void addToPendingList(String orderId);

    boolean consumeOrder(String orderId);
}
