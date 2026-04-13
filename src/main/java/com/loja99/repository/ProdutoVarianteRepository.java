package com.loja99.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loja99.entity.ProdutoVariante;

public interface ProdutoVarianteRepository extends JpaRepository<ProdutoVariante, Integer> {

    List<ProdutoVariante> findAllByProdutoIdOrderByPositionAsc(Integer produtoId);
}
