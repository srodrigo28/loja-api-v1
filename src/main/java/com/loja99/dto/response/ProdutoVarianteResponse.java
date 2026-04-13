package com.loja99.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record ProdutoVarianteResponse(
        Integer id,
        String sizeLabel,
        BigDecimal priceRetail,
        BigDecimal priceWholesale,
        BigDecimal pricePromotion,
        Integer stock,
        Integer minStock,
        Integer position
) {
}
