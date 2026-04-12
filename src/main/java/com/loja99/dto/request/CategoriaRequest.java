package com.loja99.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequest {

    @NotBlank(message = "O nome da categoria e obrigatorio.")
    @Size(max = 100, message = "O nome da categoria deve ter no maximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "A descricao da categoria e obrigatoria.")
    @Size(max = 255, message = "A descricao da categoria deve ter no maximo 255 caracteres.")
    private String descricao;

    @NotNull(message = "O usuarioId e obrigatorio.")
    private Integer usuarioId;
}
