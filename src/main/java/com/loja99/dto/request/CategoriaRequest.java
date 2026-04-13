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

    @Size(max = 255, message = "A descricao da categoria deve ter no maximo 255 caracteres.")
    private String descricao;

    @NotNull(message = "O lojaId e obrigatorio.")
    private Integer lojaId;

    @NotNull(message = "O ativo e obrigatorio.")
    private Boolean ativo;
}
