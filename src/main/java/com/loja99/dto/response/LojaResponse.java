package com.loja99.dto.response;

import lombok.Builder;

@Builder
public record LojaResponse(
        Integer id,
        String name,
        String slug,
        String ownerName,
        String ownerEmail,
        String whatsapp,
        String cnpj,
        String pixKey,
        String zipCode,
        String state,
        String city,
        String district,
        String street,
        String number,
        String complement,
        String status
) {
}
