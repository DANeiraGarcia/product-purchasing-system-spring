package co.edu.cesde.pps.web.dto.request;

import java.math.BigDecimal;

public record ProductUpsertRequest(
        Long categoryId,
        String sku,
        String name,
        String description,
        BigDecimal price,
        Integer stockQty,
        Boolean isActive
) {
}