package com.loja99.dto.response;

import lombok.Builder;

@Builder
public record AuthStoreResponse(
        Integer id,
        String name,
        String slug,
        String ownerEmail,
        String status
) {
}
