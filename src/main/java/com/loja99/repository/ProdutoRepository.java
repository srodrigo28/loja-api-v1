package com.loja99.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loja99.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    List<Produto> findAllByLojaIdOrderByNameAsc(Integer lojaId);

    boolean existsByLojaIdAndSlugIgnoreCase(Integer lojaId, String slug);

    boolean existsByLojaIdAndSlugIgnoreCaseAndIdNot(Integer lojaId, String slug, Integer id);
}
