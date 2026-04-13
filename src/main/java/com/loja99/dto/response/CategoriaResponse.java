package com.loja99.dto.response;

import lombok.Builder;

@Builder
public record CategoriaResponse(
        Integer id,
        String nome,
        String slug,
        String imageId,
        Boolean ativo,
        Integer lojaId,
        String lojaNome
) {
}
