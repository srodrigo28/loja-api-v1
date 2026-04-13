package com.loja99.dto.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class ProdutoVarianteRequest {

    private Integer id;

    @NotBlank(message = "O size_label da variante e obrigatorio.")
    @Size(max = 20, message = "O size_label da variante deve ter no maximo 20 caracteres.")
    @JsonProperty("size_label")
    private String sizeLabel;

    @DecimalMin(value = "0.0", inclusive = true, message = "O preco de varejo da variante deve ser maior ou igual a zero.")
    @JsonProperty("price_retail")
    private BigDecimal priceRetail;

    @DecimalMin(value = "0.0", inclusive = true, message = "O preco de atacado da variante deve ser maior ou igual a zero.")
    @JsonProperty("price_wholesale")
    private BigDecimal priceWholesale;

    @DecimalMin(value = "0.0", inclusive = true, message = "O preco promocional da variante deve ser maior ou igual a zero.")
    @JsonProperty("price_promotion")
    private BigDecimal pricePromotion;

    @NotNull(message = "O estoque da variante e obrigatorio.")
    @Min(value = 0, message = "O estoque da variante deve ser maior ou igual a zero.")
    private Integer stock;

    @NotNull(message = "O estoque minimo da variante e obrigatorio.")
    @Min(value = 0, message = "O estoque minimo da variante deve ser maior ou igual a zero.")
    @JsonProperty("min_stock")
    private Integer minStock;

    @Min(value = 1, message = "A position da variante deve ser maior que zero.")
    private Integer position;
}
