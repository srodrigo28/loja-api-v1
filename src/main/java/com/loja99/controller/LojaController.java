package com.loja99.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.loja99.dto.request.LojaRequest;
import com.loja99.dto.response.LojaResponse;
import com.loja99.service.LojaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/lojas")
@RequiredArgsConstructor
public class LojaController {

    private final LojaService lojaService;

    @PostMapping
    public ResponseEntity<LojaResponse> criar(@Valid @RequestBody LojaRequest request) {
        LojaResponse response = lojaService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LojaResponse>> listar() {
        return ResponseEntity.ok(lojaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LojaResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(lojaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LojaResponse> atualizar(@PathVariable Integer id, @Valid @RequestBody LojaRequest request) {
        return ResponseEntity.ok(lojaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        lojaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
