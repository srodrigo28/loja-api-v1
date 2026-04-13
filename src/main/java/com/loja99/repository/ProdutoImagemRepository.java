package com.loja99.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loja99.entity.ProdutoImagem;

public interface ProdutoImagemRepository extends JpaRepository<ProdutoImagem, Integer> {

    Optional<ProdutoImagem> findByIdAndProdutoId(Integer id, Integer produtoId);
}
