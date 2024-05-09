package com.hixtrip.sample.infra;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.order.repository.OrderRepository;
import com.hixtrip.sample.infra.db.convertor.OrderDOConvertor;
import com.hixtrip.sample.infra.db.dataobject.OrderDO;
import com.hixtrip.sample.infra.db.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.hixtrip.sample.infra.constant.RedisKeyConstant.HIXTRIP_ORDER_PENDING_KEY;

/**
 * infra层是domain定义的接口具体的实现
 */
@Component
public class OrderRepositoryImpl implements OrderRepository {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public String save(Order order){
        OrderDO orderDO = OrderDOConvertor.INSTANCE.doToEntity(order);
        orderMapper.insert(orderDO);
        return orderDO.getId();
    }

    public void update(Order order){
        orderMapper.updateById(OrderDOConvertor.INSTANCE.doToEntity(order));
    }

    /**
     * 订单放入待支付列表中，15分钟到期
     * @param orderId
     */
    public void addToPendingList(String orderId){
        BoundZSetOperations<String, Object> boundZSetOperations = redisTemplate.boundZSetOps(HIXTRIP_ORDER_PENDING_KEY);
        boundZSetOperations.add(orderId, System.currentTimeMillis() + 15 * 60 * 1000);
    }

    /**
     * 待支付订单消费
     * @param orderId
     * @return
     */
    public boolean consumeOrder(String orderId){
        BoundZSetOperations<String, Object> boundZSetOperations = redisTemplate.boundZSetOps(HIXTRIP_ORDER_PENDING_KEY);
        Long remove = boundZSetOperations.remove(orderId);
        return Objects.equals(1L,remove);
    }

    /**
     * 根据订单
     * @param orderId
     * @return
     */
    public Order getOrderById(String orderId){
        OrderDO orderDO = orderMapper.selectById(orderId);
        return OrderDOConvertor.INSTANCE.doToDomain(orderDO);
    }

    /**
     * 查询订单id列表
     * @param order
     * @param currPage
     * @param pageSize
     * @return
     */
    public List<String> selectIdsPage(Order order,int currPage,int pageSize){
        Page page = Page.of(currPage,pageSize);
        QueryWrapper<OrderDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id");
        queryWrapper.orderByDesc("createTime");
        return orderMapper.selectPage(page, queryWrapper).getRecords();
    }
}
