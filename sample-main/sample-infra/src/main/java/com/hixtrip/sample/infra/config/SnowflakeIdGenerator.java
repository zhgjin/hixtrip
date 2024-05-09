package com.hixtrip.sample.infra.config;

import org.springframework.data.keyvalue.core.IdentifierGenerator;
import org.springframework.data.util.TypeInformation;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SnowflakeIdGenerator implements IdentifierGenerator {
    @Override
    public <String> String generateIdentifierOfType(TypeInformation<String> type) {
        System.out.println(type.getType().toString());
        return (String) UUID.randomUUID().toString();
    }
}
