package com.loja99.dto.response;

import lombok.Builder;

@Builder
public record EnderecoResponse(
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado
) {
}
