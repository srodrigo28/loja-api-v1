package com.loja99.service;

import java.text.Normalizer;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final CategoriaImageStorageService categoriaImageStorageService;

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request, MultipartFile image) {
        Loja loja = lojaService.buscarEntidade(request.getLojaId());
        validarNomeDuplicado(loja.getId(), request.getNome(), null);
        String imagePath = categoriaImageStorageService.storeRequiredImage(image);
        Categoria categoria = categoriaMapper.toEntity(request, loja, imagePath);
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
    public CategoriaResponse atualizar(Integer id, CategoriaRequest request, MultipartFile image) {
        Categoria categoria = buscarEntidade(id);
        Loja loja = lojaService.buscarEntidade(request.getLojaId());
        validarNomeDuplicado(loja.getId(), request.getNome(), id);

        String imagePath = categoria.getImage();
        if (image != null && !image.isEmpty()) {
            imagePath = categoriaImageStorageService.replaceImage(categoria.getImage(), image);
        } else if (imagePath == null || imagePath.isBlank()) {
            throw new BusinessException("A imagem da categoria e obrigatoria.");
        }

        categoriaMapper.updateEntity(categoria, request, loja, imagePath);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void deletar(Integer id) {
        Categoria categoria = buscarEntidade(id);
        categoriaImageStorageService.deleteImage(categoria.getImage());
        categoriaRepository.delete(categoria);
    }

    @Transactional(readOnly = true)
    public Categoria buscarEntidade(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada para o id " + id + "."));
    }

    private void validarNomeDuplicado(Integer lojaId, String nome, Integer idIgnorado) {
        String normalizedSlug = normalizeSlug(nome);
        boolean duplicado = idIgnorado == null
                ? categoriaRepository.existsByLojaIdAndSlugIgnoreCase(lojaId, normalizedSlug)
                : categoriaRepository.existsByLojaIdAndSlugIgnoreCaseAndIdNot(lojaId, normalizedSlug, idIgnorado);

        if (duplicado) {
            throw new BusinessException("Ja existe uma categoria cadastrada com este nome nesta loja.");
        }
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
