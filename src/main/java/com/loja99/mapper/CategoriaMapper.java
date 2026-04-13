package com.loja99.mapper;

import java.text.Normalizer;

import org.springframework.stereotype.Component;

import com.loja99.dto.request.CategoriaRequest;
import com.loja99.dto.response.CategoriaResponse;
import com.loja99.entity.Categoria;
import com.loja99.entity.Loja;

@Component
public class CategoriaMapper {

    public Categoria toEntity(CategoriaRequest request, Loja loja, String imagePath) {
        return Categoria.builder()
                .nome(request.getNome().trim())
                .descricao(normalizeNullable(request.getDescricao()))
                .slug(normalizeSlug(request.getNome()))
                .image(normalizeNullable(imagePath))
                .ativo(Boolean.TRUE.equals(request.getAtivo()))
                .loja(loja)
                .build();
    }

    public void updateEntity(Categoria entity, CategoriaRequest request, Loja loja, String imagePath) {
        entity.setNome(request.getNome().trim());
        entity.setDescricao(normalizeNullable(request.getDescricao()));
        entity.setSlug(normalizeSlug(request.getNome()));
        entity.setImage(normalizeNullable(imagePath));
        entity.setAtivo(Boolean.TRUE.equals(request.getAtivo()));
        entity.setLoja(loja);
    }

    public CategoriaResponse toResponse(Categoria entity) {
        return CategoriaResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .slug(entity.getSlug())
                .image(entity.getImage())
                .ativo(entity.isAtivo())
                .lojaId(entity.getLoja().getId())
                .lojaNome(entity.getLoja().getName())
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
