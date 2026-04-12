package com.loja99.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record UsuarioResponse(
        Integer id,
        String nome,
        String email,
        String telefone,
        EnderecoResponse endereco,
        List<CategoriaResumoResponse> categorias
) {
}
