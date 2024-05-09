package com.hixtrip.sample.infra;

import com.hixtrip.sample.domain.inventory.model.Inventory;
import com.hixtrip.sample.domain.inventory.repository.InventoryRepository;
import com.hixtrip.sample.infra.db.convertor.InventoryDOConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static com.hixtrip.sample.infra.constant.RedisKeyConstant.HIXTRIP_INVENTORY_KEY;

/**
 * infra层是domain定义的接口具体的实现
 */
@Component
public class InventoryRepositoryImpl implements InventoryRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Inventory getInventory(String skuId) {
        Map<Object, Object> entries = redisTemplate.opsForHash()
                .entries(MessageFormat.format(HIXTRIP_INVENTORY_KEY, skuId));
        return InventoryDOConvertor.INSTANCE.doToDomain(entries);
    }

    /**
     * 扣减库存，当可售库存不足时扣减失败
     * @param inventory
     * @return
     */
    public boolean changeInventory(Inventory inventory) {
        String luaScript = "local currSellableQuantity=redis.call('HGET',KEYS[1],'sellableQuantity');\n" +
                "        currSellableQuantity=(currSellableQuantity ~=nil and currSellableQuantity) or 0" +
                "        if (currSellableQuantity+currSellableQuantity)<=0 then\n" +
                "            return -1;\n" +
                "        else\n" +
                "            redis.call('HINCRBY',KEYS[1],'sellableQuantity',ARGV[1]);\n" +
                "            redis.call('HINCRBY',KEYS[1],'withholdingQuantity',ARGV[2]);\n" +
                "            redis.call('HINCRBY',KEYS[1],'occupiedQuantity',ARGV[3]);" +
                "            return 1;" +
                "end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(MessageFormat.format(HIXTRIP_INVENTORY_KEY, inventory.getSkuId()))
                ,inventory.getSellableQuantity(),inventory.getWithholdingQuantity(),inventory.getOccupiedQuantity());
        return Objects.equals(1L,result);
    }
}
