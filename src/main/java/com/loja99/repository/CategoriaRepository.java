package com.loja99.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loja99.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
