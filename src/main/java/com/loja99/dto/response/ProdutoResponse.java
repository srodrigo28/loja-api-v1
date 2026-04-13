package com.loja99.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record ProdutoResponse(
        Integer id,
        Integer storeId,
        Integer categoryId,
        String name,
        String slug,
        String descriptionShort,
        String descriptionLong,
        String sku,
        BigDecimal priceRetail,
        BigDecimal priceWholesale,
        BigDecimal pricePromotion,
        Integer stock,
        Integer minStock,
        String status,
        Boolean isFeatured,
        String notes,
        String mainImageUrl,
        List<ProdutoImagemResponse> images,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
