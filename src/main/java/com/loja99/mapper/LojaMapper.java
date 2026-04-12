package com.loja99.mapper;

import org.springframework.stereotype.Component;

import com.loja99.dto.request.LojaRequest;
import com.loja99.dto.response.LojaResponse;
import com.loja99.entity.Endereco;
import com.loja99.entity.Loja;

@Component
public class LojaMapper {

    public Loja toEntity(LojaRequest request, String passwordHash) {
        return Loja.builder()
                .name(request.getName().trim())
                .slug(normalizeSlug(request.getSlug()))
                .ownerName(request.getOwnerName().trim())
                .ownerEmail(request.getOwnerEmail().trim().toLowerCase())
                .passwordHash(passwordHash)
                .whatsapp(request.getWhatsapp().trim())
                .cnpj(onlyDigits(request.getCnpj()))
                .pixKey(request.getPixKey().trim())
                .status(resolveStatus(request.getStatus()))
                .endereco(toEndereco(request))
                .build();
    }

    public void updateEntity(Loja entity, LojaRequest request, String passwordHash) {
        entity.setName(request.getName().trim());
        entity.setSlug(normalizeSlug(request.getSlug()));
        entity.setOwnerName(request.getOwnerName().trim());
        entity.setOwnerEmail(request.getOwnerEmail().trim().toLowerCase());
        entity.setPasswordHash(passwordHash);
        entity.setWhatsapp(request.getWhatsapp().trim());
        entity.setCnpj(onlyDigits(request.getCnpj()));
        entity.setPixKey(request.getPixKey().trim());
        entity.setStatus(resolveStatus(request.getStatus()));
        entity.setEndereco(toEndereco(request));
    }

    public LojaResponse toResponse(Loja entity) {
        return LojaResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .ownerName(entity.getOwnerName())
                .ownerEmail(entity.getOwnerEmail())
                .whatsapp(entity.getWhatsapp())
                .cnpj(entity.getCnpj())
                .pixKey(entity.getPixKey())
                .zipCode(entity.getEndereco().getCep())
                .state(entity.getEndereco().getEstado())
                .city(entity.getEndereco().getCidade())
                .district(entity.getEndereco().getBairro())
                .street(entity.getEndereco().getLogradouro())
                .number(entity.getEndereco().getNumero())
                .complement(entity.getEndereco().getComplemento())
                .status(entity.getStatus())
                .build();
    }

    private Endereco toEndereco(LojaRequest request) {
        return Endereco.builder()
                .cep(request.getZipCode().trim())
                .logradouro(request.getStreet().trim())
                .numero(request.getNumber().trim())
                .complemento(request.getComplement() == null ? null : request.getComplement().trim())
                .bairro(request.getDistrict().trim())
                .cidade(request.getCity().trim())
                .estado(request.getState().trim().toUpperCase())
                .build();
    }

    private String resolveStatus(String status) {
        if (status == null || status.isBlank()) {
            return "draft";
        }
        return status.trim().toLowerCase();
    }

    private String normalizeSlug(String slug) {
        return slug.trim().toLowerCase();
    }

    private String onlyDigits(String value) {
        return value.replaceAll("\\D", "");
    }
}
