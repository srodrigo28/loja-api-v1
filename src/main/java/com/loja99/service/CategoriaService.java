package com.loja99.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loja99.dto.request.CategoriaRequest;
import com.loja99.dto.response.CategoriaResponse;
import com.loja99.entity.Categoria;
import com.loja99.entity.Loja;
import com.loja99.exception.BusinessException;
import com.loja99.exception.ResourceNotFoundException;
import com.loja99.mapper.CategoriaMapper;
import com.loja99.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    private final LojaService lojaService;

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        Loja loja = lojaService.buscarEntidade(request.getLojaId());
        validarSlugDuplicado(loja.getId(), request.getSlug(), null);
        Categoria categoria = categoriaMapper.toEntity(request, loja);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar(Integer lojaId) {
        List<Categoria> categorias = lojaId == null
                ? categoriaRepository.findAll()
                : categoriaRepository.findAllByLojaIdOrderByNomeAsc(lojaId);

        return categorias.stream()
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
        Loja loja = lojaService.buscarEntidade(request.getLojaId());
        validarSlugDuplicado(loja.getId(), request.getSlug(), id);
        categoriaMapper.updateEntity(categoria, request, loja);
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

    private void validarSlugDuplicado(Integer lojaId, String slug, Integer idIgnorado) {
        String normalizedSlug = slug.trim().toLowerCase();
        boolean duplicado = idIgnorado == null
                ? categoriaRepository.existsByLojaIdAndSlugIgnoreCase(lojaId, normalizedSlug)
                : categoriaRepository.existsByLojaIdAndSlugIgnoreCaseAndIdNot(lojaId, normalizedSlug, idIgnorado);

        if (duplicado) {
            throw new BusinessException("Ja existe uma categoria cadastrada com este slug nesta loja.");
        }
    }
}
