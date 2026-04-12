package com.loja99.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loja99.dto.request.UsuarioRequest;
import com.loja99.dto.response.UsuarioResponse;
import com.loja99.entity.Usuario;
import com.loja99.exception.BusinessException;
import com.loja99.exception.ResourceNotFoundException;
import com.loja99.mapper.UsuarioMapper;
import com.loja99.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        validarEmailDuplicado(request.getEmail(), null);
        Usuario usuario = usuarioMapper.toEntity(request);
        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Integer id) {
        return usuarioMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponse atualizar(Integer id, UsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);
        validarEmailDuplicado(request.getEmail(), id);
        usuarioMapper.updateEntity(usuario, request);
        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletar(Integer id) {
        Usuario usuario = buscarEntidade(id);
        usuarioRepository.delete(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidade(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado para o id " + id + "."));
    }

    private void validarEmailDuplicado(String email, Integer idIgnorado) {
        boolean duplicado = idIgnorado == null
                ? usuarioRepository.existsByEmailIgnoreCase(email)
                : usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, idIgnorado);

        if (duplicado) {
            throw new BusinessException("Ja existe um usuario cadastrado com este email.");
        }
    }
}
