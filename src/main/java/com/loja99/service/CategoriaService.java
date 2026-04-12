package com.loja99.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loja99.dto.request.CategoriaRequest;
import com.loja99.dto.response.CategoriaResponse;
import com.loja99.entity.Categoria;
import com.loja99.entity.Usuario;
import com.loja99.exception.ResourceNotFoundException;
import com.loja99.mapper.CategoriaMapper;
import com.loja99.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    private final UsuarioService usuarioService;

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        Usuario usuario = usuarioService.buscarEntidade(request.getUsuarioId());
        Categoria categoria = categoriaMapper.toEntity(request, usuario);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Integer id) {
        return categoriaMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public CategoriaResponse atualizar(Integer id, CategoriaRequest request) {
        Categoria categoria = buscarEntidade(id);
        Usuario usuario = usuarioService.buscarEntidade(request.getUsuarioId());
        categoriaMapper.updateEntity(categoria, request, usuario);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void deletar(Integer id) {
        Categoria categoria = buscarEntidade(id);
        categoriaRepository.delete(categoria);
    }

    @Transactional(readOnly = true)
    public Categoria buscarEntidade(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada para o id " + id + "."));
    }
}
