package com.loja99.dto.response;

import lombok.Builder;

@Builder
public record CategoriaResumoResponse(
        Integer id,
        String nome,
        String slug
) {
}
