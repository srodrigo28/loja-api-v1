package com.loja99.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.loja99.dto.request.ProdutoRequest;
import com.loja99.dto.response.ApiDataResponse;
import com.loja99.dto.response.ProdutoImagemResponse;
import com.loja99.dto.response.ProdutoResponse;
import com.loja99.service.ProdutoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiDataResponse<ProdutoResponse>> criar(@Valid @RequestBody ProdutoRequest request) {
        ProdutoResponse response = produtoService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(new ApiDataResponse<>(response));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar(@RequestParam(value = "store_id", required = false) Integer storeId) {
        return ResponseEntity.ok(produtoService.listar(storeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiDataResponse<ProdutoResponse>> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(new ApiDataResponse<>(produtoService.buscarPorId(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiDataResponse<ProdutoResponse>> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ProdutoRequest request
    ) {
        return ResponseEntity.ok(new ApiDataResponse<>(produtoService.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiDataResponse<String>> deletar(@PathVariable Integer id) {
        produtoService.deletar(id);
        return ResponseEntity.ok(new ApiDataResponse<>("Produto removido com sucesso."));
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiDataResponse<List<ProdutoImagemResponse>>> adicionarImagens(
            @PathVariable Integer id,
            @RequestParam("images") List<MultipartFile> images
    ) {
        return ResponseEntity.ok(new ApiDataResponse<>(produtoService.adicionarImagens(id, images)));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<ApiDataResponse<String>> removerImagem(@PathVariable Integer id, @PathVariable Integer imageId) {
        produtoService.removerImagem(id, imageId);
        return ResponseEntity.ok(new ApiDataResponse<>("Imagem removida com sucesso."));
    }

    @PostMapping("/{id}/images/{imageId}/set-main")
    public ResponseEntity<ApiDataResponse<String>> definirImagemPrincipal(@PathVariable Integer id, @PathVariable Integer imageId) {
        produtoService.definirImagemPrincipal(id, imageId);
        return ResponseEntity.ok(new ApiDataResponse<>("Imagem principal atualizada com sucesso."));
    }
}
