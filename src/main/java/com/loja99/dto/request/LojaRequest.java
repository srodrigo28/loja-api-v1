package com.loja99.dto.request;

import jakarta.validation.constraints.Email;
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
public class LojaRequest {

    @NotBlank(message = "O nome da loja e obrigatorio.")
    @Size(min = 3, max = 160, message = "O nome da loja deve ter entre 3 e 160 caracteres.")
    private String name;

    @NotBlank(message = "O slug da loja e obrigatorio.")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "O slug deve conter apenas letras minusculas, numeros e hifens.")
    @Size(max = 160, message = "O slug da loja deve ter no maximo 160 caracteres.")
    private String slug;

    @NotBlank(message = "O nome do responsavel e obrigatorio.")
    @Size(min = 3, max = 120, message = "O nome do responsavel deve ter entre 3 e 120 caracteres.")
    private String ownerName;

    @NotBlank(message = "O email do responsavel e obrigatorio.")
    @Email(message = "O email do responsavel e invalido.")
    @Size(max = 120, message = "O email do responsavel deve ter no maximo 120 caracteres.")
    private String ownerEmail;

    @NotBlank(message = "A senha e obrigatoria.")
    @Size(min = 6, max = 120, message = "A senha deve ter entre 6 e 120 caracteres.")
    private String password;

    @NotBlank(message = "O WhatsApp da loja e obrigatorio.")
    @Pattern(regexp = "^\\+?[0-9()\\-\\s]{10,20}$", message = "O WhatsApp informado e invalido.")
    private String whatsapp;

    @NotBlank(message = "O CNPJ e obrigatorio.")
    @Pattern(regexp = "^(\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})$", message = "O CNPJ deve estar no formato 99.999.999/9999-99 ou conter 14 digitos.")
    private String cnpj;

    @NotBlank(message = "A chave Pix e obrigatoria.")
    @Size(max = 120, message = "A chave Pix deve ter no maximo 120 caracteres.")
    private String pixKey;

    @NotBlank(message = "O CEP e obrigatorio.")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "O CEP deve estar no formato 99999-999 ou 99999999.")
    private String zipCode;

    @NotBlank(message = "O estado e obrigatorio.")
    @Pattern(regexp = "[A-Za-z]{2}", message = "O estado deve conter 2 letras.")
    private String state;

    @NotBlank(message = "A cidade e obrigatoria.")
    @Size(max = 100, message = "A cidade deve ter no maximo 100 caracteres.")
    private String city;

    @NotBlank(message = "O bairro e obrigatorio.")
    @Size(max = 100, message = "O bairro deve ter no maximo 100 caracteres.")
    private String district;

    @NotBlank(message = "O logradouro e obrigatorio.")
    @Size(max = 150, message = "O logradouro deve ter no maximo 150 caracteres.")
    private String street;

    @NotBlank(message = "O numero e obrigatorio.")
    @Size(max = 20, message = "O numero deve ter no maximo 20 caracteres.")
    private String number;

    @Size(max = 100, message = "O complemento deve ter no maximo 100 caracteres.")
    private String complement;

    @Pattern(regexp = "^(draft|active|inactive)$", message = "O status deve ser draft, active ou inactive.")
    private String status;
}
