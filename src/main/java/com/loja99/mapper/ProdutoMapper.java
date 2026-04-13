package com.loja99.mapper;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.loja99.dto.request.ProdutoRequest;
import com.loja99.dto.response.ProdutoImagemResponse;
import com.loja99.dto.response.ProdutoResponse;
import com.loja99.entity.Categoria;
import com.loja99.entity.Loja;
import com.loja99.entity.Produto;
import com.loja99.entity.ProdutoImagem;

@Component
public class ProdutoMapper {

    public Produto toEntity(ProdutoRequest request, Loja loja, Categoria categoria) {
        return Produto.builder()
                .loja(loja)
                .categoria(categoria)
                .name(request.getName().trim())
                .slug(normalizeSlug(request.getSlug()))
                .descriptionShort(normalizeNullable(request.getDescriptionShort()))
                .descriptionLong(normalizeNullable(request.getDescriptionLong()))
                .sku(normalizeNullable(request.getSku()))
                .priceRetail(request.getPriceRetail())
                .priceWholesale(request.getPriceWholesale())
                .pricePromotion(request.getPricePromotion())
                .stock(request.getStock())
                .minStock(request.getMinStock())
                .status(request.getStatus().trim().toLowerCase())
                .featured(Boolean.TRUE.equals(request.getIsFeatured()))
                .notes(normalizeNullable(request.getNotes()))
                .build();
    }

    public void updateEntity(Produto entity, ProdutoRequest request, Loja loja, Categoria categoria) {
        entity.setLoja(loja);
        entity.setCategoria(categoria);
        entity.setName(request.getName().trim());
        entity.setSlug(normalizeSlug(request.getSlug()));
        entity.setDescriptionShort(normalizeNullable(request.getDescriptionShort()));
        entity.setDescriptionLong(normalizeNullable(request.getDescriptionLong()));
        entity.setSku(normalizeNullable(request.getSku()));
        entity.setPriceRetail(request.getPriceRetail());
        entity.setPriceWholesale(request.getPriceWholesale());
        entity.setPricePromotion(request.getPricePromotion());
        entity.setStock(request.getStock());
        entity.setMinStock(request.getMinStock());
        entity.setStatus(request.getStatus().trim().toLowerCase());
        entity.setFeatured(Boolean.TRUE.equals(request.getIsFeatured()));
        entity.setNotes(normalizeNullable(request.getNotes()));
    }

    public ProdutoResponse toResponse(Produto entity) {
        List<ProdutoImagemResponse> images = entity.getImages().stream()
                .sorted(Comparator.comparing(ProdutoImagem::getPosition))
                .map(this::toImageResponse)
                .toList();

        String mainImageUrl = entity.getImages().stream()
                .filter(ProdutoImagem::isMain)
                .min(Comparator.comparing(ProdutoImagem::getPosition))
                .map(ProdutoImagem::getImageUrl)
                .orElseGet(() -> images.isEmpty() ? null : images.get(0).imageUrl());

        return ProdutoResponse.builder()
                .id(entity.getId())
                .storeId(entity.getLoja().getId())
                .categoryId(entity.getCategoria().getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .descriptionShort(entity.getDescriptionShort())
                .descriptionLong(entity.getDescriptionLong())
                .sku(entity.getSku())
                .priceRetail(entity.getPriceRetail())
                .priceWholesale(entity.getPriceWholesale())
                .pricePromotion(entity.getPricePromotion())
                .stock(entity.getStock())
                .minStock(entity.getMinStock())
                .status(entity.getStatus())
                .isFeatured(entity.isFeatured())
                .notes(entity.getNotes())
                .mainImageUrl(mainImageUrl)
                .images(images)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ProdutoImagemResponse toImageResponse(ProdutoImagem entity) {
        return ProdutoImagemResponse.builder()
                .id(entity.getId())
                .filename(entity.getFilename())
                .imageUrl(entity.getImageUrl())
                .isMain(entity.isMain())
                .position(entity.getPosition())
                .build();
    }

    private String normalizeNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String normalizeSlug(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}
