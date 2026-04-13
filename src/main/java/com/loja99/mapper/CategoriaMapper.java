package com.loja99.mapper;

import org.springframework.stereotype.Component;

import com.loja99.dto.request.CategoriaRequest;
import com.loja99.dto.response.CategoriaResponse;
import com.loja99.entity.Categoria;
import com.loja99.entity.Loja;

@Component
public class CategoriaMapper {

    public Categoria toEntity(CategoriaRequest request, Loja loja) {
        return Categoria.builder()
                .nome(request.getNome().trim())
                .slug(request.getSlug().trim().toLowerCase())
                .imageId(normalizeNullable(request.getImageId()))
                .ativo(Boolean.TRUE.equals(request.getAtivo()))
                .loja(loja)
                .build();
    }

    public void updateEntity(Categoria entity, CategoriaRequest request, Loja loja) {
        entity.setNome(request.getNome().trim());
        entity.setSlug(request.getSlug().trim().toLowerCase());
        entity.setImageId(normalizeNullable(request.getImageId()));
        entity.setAtivo(Boolean.TRUE.equals(request.getAtivo()));
        entity.setLoja(loja);
    }

    public CategoriaResponse toResponse(Categoria entity) {
        return CategoriaResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .slug(entity.getSlug())
                .imageId(entity.getImageId())
                .ativo(entity.isAtivo())
                .lojaId(entity.getLoja().getId())
                .lojaNome(entity.getLoja().getName())
                .build();
    }

    private String normalizeNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
