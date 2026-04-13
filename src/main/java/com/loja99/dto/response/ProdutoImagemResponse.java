package com.loja99.dto.response;

import lombok.Builder;

@Builder
public record ProdutoImagemResponse(
        Integer id,
        String filename,
        String imageUrl,
        Boolean isMain,
        Integer position
) {
}
