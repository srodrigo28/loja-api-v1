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
public class ProdutoRequest {

    @NotNull(message = "O store_id e obrigatorio.")
    @JsonProperty("store_id")
    private Integer storeId;

    @NotNull(message = "O category_id e obrigatorio.")
    @JsonProperty("category_id")
    private Integer categoryId;

    @NotBlank(message = "O nome do produto e obrigatorio.")
    @Size(max = 160, message = "O nome do produto deve ter no maximo 160 caracteres.")
    private String name;

    @NotBlank(message = "O slug do produto e obrigatorio.")
    @Size(max = 160, message = "O slug do produto deve ter no maximo 160 caracteres.")
    private String slug;

    @Size(max = 500, message = "A descricao curta deve ter no maximo 500 caracteres.")
    @JsonProperty("description_short")
    private String descriptionShort;

    @JsonProperty("description_long")
    private String descriptionLong;

    @Size(max = 80, message = "O SKU deve ter no maximo 80 caracteres.")
    private String sku;

    @NotNull(message = "O price_retail e obrigatorio.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O preco de varejo deve ser maior ou igual a zero.")
    @JsonProperty("price_retail")
    private BigDecimal priceRetail;

    @DecimalMin(value = "0.0", inclusive = true, message = "O preco de atacado deve ser maior ou igual a zero.")
    @JsonProperty("price_wholesale")
    private BigDecimal priceWholesale;

    @DecimalMin(value = "0.0", inclusive = true, message = "O preco promocional deve ser maior ou igual a zero.")
    @JsonProperty("price_promotion")
    private BigDecimal pricePromotion;

    @NotNull(message = "O estoque do produto e obrigatorio.")
    @Min(value = 0, message = "O estoque do produto deve ser maior ou igual a zero.")
    private Integer stock;

    @NotNull(message = "O estoque minimo do produto e obrigatorio.")
    @Min(value = 0, message = "O estoque minimo do produto deve ser maior ou igual a zero.")
    @JsonProperty("min_stock")
    private Integer minStock;

    @NotBlank(message = "O status do produto e obrigatorio.")
    @Size(max = 40, message = "O status do produto deve ter no maximo 40 caracteres.")
    private String status;

    @NotNull(message = "O is_featured e obrigatorio.")
    @JsonProperty("is_featured")
    private Boolean isFeatured;

    @Size(max = 500, message = "As notas do produto devem ter no maximo 500 caracteres.")
    private String notes;
}
