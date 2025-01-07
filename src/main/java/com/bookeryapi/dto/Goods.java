package com.bookeryapi.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record Goods(
        UUID id,
        String name,
        BigDecimal price,
        Integer totalCount,
        Integer soldCount,
        boolean deleted) {
}
