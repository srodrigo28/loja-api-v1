package com.loja99.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loja99.dto.request.LojaRequest;
import com.loja99.dto.response.LojaResponse;
import com.loja99.entity.Loja;
import com.loja99.exception.BusinessException;
import com.loja99.exception.ResourceNotFoundException;
import com.loja99.mapper.LojaMapper;
import com.loja99.repository.LojaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LojaService {

    private final LojaRepository lojaRepository;
    private final LojaMapper lojaMapper;
    private final PasswordHashService passwordHashService;

    @Transactional
    public LojaResponse criar(LojaRequest request) {
        validarDuplicidades(request, null);
        Loja loja = lojaMapper.toEntity(request, passwordHashService.hash(request.getPassword()));
        return lojaMapper.toResponse(lojaRepository.save(loja));
    }

    @Transactional(readOnly = true)
    public List<LojaResponse> listar() {
        return lojaRepository.findAll().stream()
                .map(lojaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LojaResponse buscarPorId(Integer id) {
        return lojaMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public LojaResponse atualizar(Integer id, LojaRequest request) {
        Loja loja = buscarEntidade(id);
        validarDuplicidades(request, id);
        lojaMapper.updateEntity(loja, request, passwordHashService.hash(request.getPassword()));
        return lojaMapper.toResponse(lojaRepository.save(loja));
    }

    @Transactional
    public void deletar(Integer id) {
        Loja loja = buscarEntidade(id);
        lojaRepository.delete(loja);
    }

    @Transactional(readOnly = true)
    public Loja buscarEntidade(Integer id) {
        return lojaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loja nao encontrada para o id " + id + "."));
    }

    private void validarDuplicidades(LojaRequest request, Integer idIgnorado) {
        String slug = request.getSlug().trim().toLowerCase();
        String ownerEmail = request.getOwnerEmail().trim().toLowerCase();
        String cnpj = request.getCnpj().replaceAll("\\D", "");

        boolean slugDuplicado = idIgnorado == null
                ? lojaRepository.existsBySlugIgnoreCase(slug)
                : lojaRepository.existsBySlugIgnoreCaseAndIdNot(slug, idIgnorado);

        if (slugDuplicado) {
            throw new BusinessException("Ja existe uma loja cadastrada com este slug.");
        }

        boolean emailDuplicado = idIgnorado == null
                ? lojaRepository.existsByOwnerEmailIgnoreCase(ownerEmail)
                : lojaRepository.existsByOwnerEmailIgnoreCaseAndIdNot(ownerEmail, idIgnorado);

        if (emailDuplicado) {
            throw new BusinessException("Ja existe uma loja cadastrada com este email de responsavel.");
        }

        boolean cnpjDuplicado = idIgnorado == null
                ? lojaRepository.existsByCnpj(cnpj)
                : lojaRepository.existsByCnpjAndIdNot(cnpj, idIgnorado);

        if (cnpjDuplicado) {
            throw new BusinessException("Ja existe uma loja cadastrada com este CNPJ.");
        }
    }
}
