package com.loja99.dto.response;

import lombok.Builder;

@Builder
public record AuthSessionResponse(
        boolean authenticated,
        String token,
        AuthUserResponse user,
        AuthStoreResponse store
) {
}
