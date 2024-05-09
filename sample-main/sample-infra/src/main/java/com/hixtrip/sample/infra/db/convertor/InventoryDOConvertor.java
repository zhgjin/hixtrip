package com.hixtrip.sample.infra.db.convertor;

import com.hixtrip.sample.domain.inventory.model.Inventory;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Map;

public interface InventoryDOConvertor {
    InventoryDOConvertor INSTANCE = Mappers.getMapper(InventoryDOConvertor.class);

    @Mappings({
            @Mapping(source = "sellableQuantity",target = "sellableQuantity"),
            @Mapping(source = "withholdingQuantity",target = "withholdingQuantity"),
            @Mapping(source = "occupiedQuantity",target = "occupiedQuantity"),
    })
    Inventory doToDomain(Map InventoryDO);

}
