package com.loja99.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.loja99.dto.request.EnderecoRequest;
import com.loja99.dto.request.UsuarioRequest;
import com.loja99.dto.response.EnderecoResponse;
import com.loja99.dto.response.UsuarioResponse;
import com.loja99.entity.Endereco;
import com.loja99.entity.Usuario;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequest request) {
        return Usuario.builder()
                .nome(request.getNome().trim())
                .email(request.getEmail().trim().toLowerCase())
                .telefone(request.getTelefone().trim())
                .endereco(toEndereco(request.getEndereco()))
                .build();
    }

    public void updateEntity(Usuario entity, UsuarioRequest request) {
        entity.setNome(request.getNome().trim());
        entity.setEmail(request.getEmail().trim().toLowerCase());
        entity.setTelefone(request.getTelefone().trim());
        entity.setEndereco(toEndereco(request.getEndereco()));
    }

    public UsuarioResponse toResponse(Usuario entity) {
        return UsuarioResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .telefone(entity.getTelefone())
                .endereco(toEnderecoResponse(entity.getEndereco()))
                .categorias(List.of())
                .build();
    }

    private Endereco toEndereco(EnderecoRequest request) {
        return Endereco.builder()
                .cep(request.getCep().trim())
                .logradouro(request.getLogradouro().trim())
                .numero(request.getNumero().trim())
                .complemento(request.getComplemento() == null ? null : request.getComplemento().trim())
                .bairro(request.getBairro().trim())
                .cidade(request.getCidade().trim())
                .estado(request.getEstado().trim().toUpperCase())
                .build();
    }

    private EnderecoResponse toEnderecoResponse(Endereco endereco) {
        return EnderecoResponse.builder()
                .cep(endereco.getCep())
                .logradouro(endereco.getLogradouro())
                .numero(endereco.getNumero())
                .complemento(endereco.getComplemento())
                .bairro(endereco.getBairro())
                .cidade(endereco.getCidade())
                .estado(endereco.getEstado())
                .build();
    }
}
