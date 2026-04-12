package com.loja99.mapper;

import org.springframework.stereotype.Component;

import com.loja99.dto.request.CategoriaRequest;
import com.loja99.dto.response.CategoriaResponse;
import com.loja99.entity.Categoria;
import com.loja99.entity.Usuario;

@Component
public class CategoriaMapper {

    public Categoria toEntity(CategoriaRequest request, Usuario usuario) {
        return Categoria.builder()
                .nome(request.getNome().trim())
                .descricao(request.getDescricao().trim())
                .usuario(usuario)
                .build();
    }

    public void updateEntity(Categoria entity, CategoriaRequest request, Usuario usuario) {
        entity.setNome(request.getNome().trim());
        entity.setDescricao(request.getDescricao().trim());
        entity.setUsuario(usuario);
    }

    public CategoriaResponse toResponse(Categoria entity) {
        return CategoriaResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .usuarioId(entity.getUsuario().getId())
                .usuarioNome(entity.getUsuario().getNome())
                .build();
    }
}
