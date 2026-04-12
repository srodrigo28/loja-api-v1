package com.loja99.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> index() {
        return ResponseEntity.ok(Map.of(
                "projeto", "Loja99 API",
                "versao", "0.0.1-SNAPSHOT",
                "status", "online",
                "mensagem", "Bem-vindo a API Loja99.",
                "links", Map.of(
                        "health", "/actuator/health",
                        "usuarios", "/api/usuarios",
                        "categorias", "/api/categorias"
                ),
                "recursos", List.of(
                        Map.of(
                                "nome", "usuarios",
                                "basePath", "/api/usuarios",
                                "endpoints", List.of(
                                        Map.of("metodo", "GET", "path", "/api/usuarios", "descricao", "Lista todos os usuarios"),
                                        Map.of("metodo", "GET", "path", "/api/usuarios/{id}", "descricao", "Busca um usuario por id"),
                                        Map.of("metodo", "POST", "path", "/api/usuarios", "descricao", "Cria um novo usuario"),
                                        Map.of("metodo", "PUT", "path", "/api/usuarios/{id}", "descricao", "Atualiza um usuario existente"),
                                        Map.of("metodo", "DELETE", "path", "/api/usuarios/{id}", "descricao", "Remove um usuario")
                                )
                        ),
                        Map.of(
                                "nome", "categorias",
                                "basePath", "/api/categorias",
                                "endpoints", List.of(
                                        Map.of("metodo", "GET", "path", "/api/categorias", "descricao", "Lista todas as categorias"),
                                        Map.of("metodo", "GET", "path", "/api/categorias/{id}", "descricao", "Busca uma categoria por id"),
                                        Map.of("metodo", "POST", "path", "/api/categorias", "descricao", "Cria uma nova categoria"),
                                        Map.of("metodo", "PUT", "path", "/api/categorias/{id}", "descricao", "Atualiza uma categoria existente"),
                                        Map.of("metodo", "DELETE", "path", "/api/categorias/{id}", "descricao", "Remove uma categoria")
                                )
                        )
                )));
    }
}
