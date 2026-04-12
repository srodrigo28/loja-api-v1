package com.loja99.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class EnderecoRequest {

    @NotBlank(message = "O CEP e obrigatorio.")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "O CEP deve estar no formato 99999-999 ou 99999999.")
    private String cep;

    @NotBlank(message = "O logradouro e obrigatorio.")
    @Size(max = 150, message = "O logradouro deve ter no maximo 150 caracteres.")
    private String logradouro;

    @NotBlank(message = "O numero e obrigatorio.")
    @Size(max = 20, message = "O numero deve ter no maximo 20 caracteres.")
    private String numero;

    @Size(max = 100, message = "O complemento deve ter no maximo 100 caracteres.")
    private String complemento;

    @NotBlank(message = "O bairro e obrigatorio.")
    @Size(max = 100, message = "O bairro deve ter no maximo 100 caracteres.")
    private String bairro;

    @NotBlank(message = "A cidade e obrigatoria.")
    @Size(max = 100, message = "A cidade deve ter no maximo 100 caracteres.")
    private String cidade;

    @NotBlank(message = "O estado e obrigatorio.")
    @Pattern(regexp = "[A-Za-z]{2}", message = "O estado deve conter 2 letras.")
    private String estado;
}
