package com.loja99.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loja99.entity.Loja;

public interface LojaRepository extends JpaRepository<Loja, Integer> {

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Integer id);

    boolean existsByOwnerEmailIgnoreCase(String ownerEmail);

    boolean existsByOwnerEmailIgnoreCaseAndIdNot(String ownerEmail, Integer id);

    boolean existsByCnpj(String cnpj);

    boolean existsByCnpjAndIdNot(String cnpj, Integer id);

    Optional<Loja> findByOwnerEmailIgnoreCase(String ownerEmail);
}
