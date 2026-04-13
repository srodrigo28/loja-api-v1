package com.loja99.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LojaStatusRequest(
        @NotBlank(message = "O status da loja e obrigatorio.")
        @Pattern(regexp = "^(active|inactive)$", message = "O status deve ser active ou inactive.")
        String status
) {
}
