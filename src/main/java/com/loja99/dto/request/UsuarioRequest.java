package com.loja99.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class UsuarioRequest {

    @NotBlank(message = "O nome e obrigatorio.")
    @Size(max = 120, message = "O nome deve ter no maximo 120 caracteres.")
    private String nome;

    @NotBlank(message = "O email e obrigatorio.")
    @Email(message = "O email informado e invalido.")
    @Size(max = 120, message = "O email deve ter no maximo 120 caracteres.")
    private String email;

    @NotBlank(message = "O telefone e obrigatorio.")
    @Pattern(regexp = "^\\+?[0-9()\\-\\s]{10,20}$", message = "O telefone informado e invalido.")
    private String telefone;

    @Valid
    @NotNull(message = "O endereco e obrigatorio.")
    private EnderecoRequest endereco;
}
