package com.loja99.dto.response;

import lombok.Builder;

@Builder
public record AuthUserResponse(
        String role,
        String ownerName,
        String ownerEmail
) {
}
