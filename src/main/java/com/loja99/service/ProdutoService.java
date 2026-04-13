package com.loja99.service;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.loja99.dto.request.ProdutoRequest;
import com.loja99.dto.request.ProdutoVarianteRequest;
import com.loja99.dto.response.ProdutoImagemResponse;
import com.loja99.dto.response.ProdutoResponse;
import com.loja99.entity.Categoria;
import com.loja99.entity.Loja;
import com.loja99.entity.Produto;
import com.loja99.entity.ProdutoImagem;
import com.loja99.entity.ProdutoVariante;
import com.loja99.exception.BusinessException;
import com.loja99.exception.ResourceNotFoundException;
import com.loja99.mapper.ProdutoMapper;
import com.loja99.repository.ProdutoImagemRepository;
import com.loja99.repository.ProdutoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private static final int MAX_IMAGES = 5;

    private final ProdutoRepository produtoRepository;
    private final ProdutoImagemRepository produtoImagemRepository;
    private final ProdutoMapper produtoMapper;
    private final LojaService lojaService;
    private final CategoriaService categoriaService;
    private final ProdutoImageStorageService produtoImageStorageService;

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Loja loja = lojaService.buscarEntidade(request.getStoreId());
        Categoria categoria = validarCategoriaDaLoja(request.getCategoryId(), loja.getId());
        validarSlugDuplicado(loja.getId(), request.getSlug(), null);

        Produto produto = produtoMapper.toEntity(request, loja, categoria);
        syncVariants(produto, request.getVariants());
        aplicarResumoDeEstoque(produto, request);
        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar(Integer storeId) {
        List<Produto> produtos = storeId == null
                ? produtoRepository.findAll()
                : produtoRepository.findAllByLojaIdOrderByNameAsc(storeId);

        return produtos.stream()
                .map(produtoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Integer id) {
        return produtoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProdutoResponse atualizar(Integer id, ProdutoRequest request) {
        Produto produto = buscarEntidade(id);
        Loja loja = lojaService.buscarEntidade(request.getStoreId());
        Categoria categoria = validarCategoriaDaLoja(request.getCategoryId(), loja.getId());
        validarSlugDuplicado(loja.getId(), request.getSlug(), id);

        produtoMapper.updateEntity(produto, request, loja, categoria);
        syncVariants(produto, request.getVariants());
        aplicarResumoDeEstoque(produto, request);
        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public void deletar(Integer id) {
        Produto produto = buscarEntidade(id);
        produtoRepository.delete(produto);
        produtoImageStorageService.deleteProductDirectory(id);
    }

    @Transactional
    public List<ProdutoImagemResponse> adicionarImagens(Integer produtoId, List<MultipartFile> images) {
        Produto produto = buscarEntidade(produtoId);
        if (images == null || images.isEmpty()) {
            throw new BusinessException("Envie ao menos 1 imagem valida para o produto.");
        }

        int totalAfterUpload = produto.getImages().size() + images.size();
        if (totalAfterUpload > MAX_IMAGES) {
            throw new BusinessException("O produto aceita de 1 a 5 imagens nesta fase.");
        }

        int nextPosition = produto.getImages().stream()
                .map(ProdutoImagem::getPosition)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        for (MultipartFile image : images) {
            ProdutoImageStorageService.StoredProductImage stored = produtoImageStorageService.store(produtoId, image);
            ProdutoImagem entity = ProdutoImagem.builder()
                    .produto(produto)
                    .filename(stored.filename())
                    .imageUrl(stored.imageUrl())
                    .main(produto.getImages().isEmpty())
                    .position(nextPosition++)
                    .build();
            produto.getImages().add(entity);
        }

        Produto saved = produtoRepository.save(produto);
        return saved.getImages().stream()
                .sorted(Comparator.comparing(ProdutoImagem::getPosition))
                .map(produtoMapper::toImageResponse)
                .toList();
    }

    @Transactional
    public void definirImagemPrincipal(Integer produtoId, Integer imageId) {
        Produto produto = buscarEntidade(produtoId);
        ProdutoImagem targetImage = buscarImagem(produtoId, imageId);

        for (ProdutoImagem image : produto.getImages()) {
            image.setMain(image.getId().equals(targetImage.getId()));
        }

        produtoRepository.save(produto);
    }

    @Transactional
    public void removerImagem(Integer produtoId, Integer imageId) {
        Produto produto = buscarEntidade(produtoId);

        if (produto.getImages().size() <= 1) {
            throw new BusinessException("O produto precisa manter ao menos 1 imagem.");
        }

        ProdutoImagem image = buscarImagem(produtoId, imageId);
        boolean wasMain = image.isMain();
        String imagePath = image.getImageUrl();

        produto.getImages().removeIf(current -> current.getId().equals(image.getId()));
        reorganizarImagens(produto, wasMain);
        produtoRepository.save(produto);
        produtoImageStorageService.deleteImage(imagePath);
    }

    @Transactional(readOnly = true)
    public Produto buscarEntidade(Integer id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado para o id " + id + "."));
    }

    private Categoria validarCategoriaDaLoja(Integer categoriaId, Integer lojaId) {
        Categoria categoria = categoriaService.buscarEntidade(categoriaId);
        if (!categoria.getLoja().getId().equals(lojaId)) {
            throw new BusinessException("A categoria selecionada nao pertence a esta loja.");
        }
        return categoria;
    }

    private ProdutoImagem buscarImagem(Integer produtoId, Integer imageId) {
        return produtoImagemRepository.findByIdAndProdutoId(imageId, produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagem do produto nao encontrada para o id " + imageId + "."));
    }

    private void reorganizarImagens(Produto produto, boolean needsNewMain) {
        List<ProdutoImagem> sortedImages = produto.getImages().stream()
                .sorted(Comparator.comparing(ProdutoImagem::getPosition))
                .toList();

        for (int index = 0; index < sortedImages.size(); index++) {
            ProdutoImagem current = sortedImages.get(index);
            current.setPosition(index + 1);
            if (needsNewMain) {
                current.setMain(index == 0);
            }
        }
    }

    private void syncVariants(Produto produto, List<ProdutoVarianteRequest> requests) {
        if (requests == null) {
            return;
        }

        validarVariantesDuplicadas(requests);
        produto.getVariants().clear();

        for (int index = 0; index < requests.size(); index++) {
            ProdutoVarianteRequest request = requests.get(index);
            ProdutoVariante variante = ProdutoVariante.builder()
                    .produto(produto)
                    .sizeLabel(normalizeSizeLabel(request.getSizeLabel()))
                    .priceRetail(request.getPriceRetail())
                    .priceWholesale(request.getPriceWholesale())
                    .pricePromotion(request.getPricePromotion())
                    .stock(request.getStock())
                    .minStock(request.getMinStock())
                    .position(request.getPosition() == null ? index + 1 : request.getPosition())
                    .build();
            produto.getVariants().add(variante);
        }
    }

    private void aplicarResumoDeEstoque(Produto produto, ProdutoRequest request) {
        if (!produto.getVariants().isEmpty()) {
            int totalStock = produto.getVariants().stream()
                    .map(ProdutoVariante::getStock)
                    .reduce(0, Integer::sum);
            int totalMinStock = produto.getVariants().stream()
                    .map(ProdutoVariante::getMinStock)
                    .reduce(0, Integer::sum);
            produto.setStock(totalStock);
            produto.setMinStock(totalMinStock);
            return;
        }

        produto.setStock(request.getStock());
        produto.setMinStock(request.getMinStock());
    }

    private void validarVariantesDuplicadas(List<ProdutoVarianteRequest> requests) {
        Set<String> normalizedLabels = new HashSet<>();
        for (ProdutoVarianteRequest request : requests) {
            String sizeLabel = normalizeSizeLabel(request.getSizeLabel());
            if (!normalizedLabels.add(sizeLabel)) {
                throw new BusinessException("Nao e permitido repetir o mesmo size_label no produto.");
            }
        }
    }

    private void validarSlugDuplicado(Integer lojaId, String slug, Integer idIgnorado) {
        String normalizedSlug = normalizeSlug(slug);
        boolean duplicado = idIgnorado == null
                ? produtoRepository.existsByLojaIdAndSlugIgnoreCase(lojaId, normalizedSlug)
                : produtoRepository.existsByLojaIdAndSlugIgnoreCaseAndIdNot(lojaId, normalizedSlug, idIgnorado);

        if (duplicado) {
            throw new BusinessException("Ja existe um produto cadastrado com este slug nesta loja.");
        }
    }

    private String normalizeSizeLabel(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private String normalizeSlug(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}
