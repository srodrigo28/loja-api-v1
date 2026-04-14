package com.loja99.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.loja99.config.UploadPathResolver;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.loja99.exception.BusinessException;

@Service
public class ProdutoImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );
    private static final String PUBLIC_PREFIX = "/uploads/products/";

    private final Path productUploadDir;

    public ProdutoImageStorageService(
            @Value("${app.upload.base-dir:assets/uploads}") String uploadBaseDir,
            UploadPathResolver uploadPathResolver
    ) {
        this.productUploadDir = uploadPathResolver.resolveBaseDir(uploadBaseDir).resolve("products");
        ensureUploadDirectory(productUploadDir);
    }

    public StoredProductImage store(Integer produtoId, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BusinessException("Envie ao menos 1 imagem valida para o produto.");
        }

        validateImage(image);
        Path productDir = productUploadDir.resolve(String.valueOf(produtoId)).normalize();
        ensureUploadDirectory(productDir);

        String extension = getExtension(image.getOriginalFilename());
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path target = productDir.resolve(filename).normalize();

        if (!target.startsWith(productDir)) {
            throw new IllegalStateException("Caminho de upload invalido para produto.");
        }

        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Nao foi possivel salvar a imagem do produto.", ex);
        }

        return new StoredProductImage(filename, PUBLIC_PREFIX + produtoId + "/" + filename);
    }

    public void deleteImage(String imagePath) {
        Path target = resolveStoredPath(imagePath);
        if (target == null) {
            return;
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            throw new IllegalStateException("Nao foi possivel remover a imagem do produto.", ex);
        }
    }

    public void deleteProductDirectory(Integer produtoId) {
        Path productDir = productUploadDir.resolve(String.valueOf(produtoId)).normalize();
        if (!productDir.startsWith(productUploadDir) || !Files.exists(productDir)) {
            return;
        }

        try (var paths = Files.walk(productDir)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
        } catch (IOException ex) {
            throw new IllegalStateException("Nao foi possivel limpar as imagens do produto.", ex);
        } catch (RuntimeException ex) {
            if (ex.getCause() instanceof IOException ioEx) {
                throw new IllegalStateException("Nao foi possivel limpar as imagens do produto.", ioEx);
            }
            throw ex;
        }
    }

    private Path resolveStoredPath(String imagePath) {
        if (!StringUtils.hasText(imagePath)) {
            return null;
        }

        String normalizedPath = imagePath.trim().replace('\\', '/');
        if (!normalizedPath.startsWith(PUBLIC_PREFIX)) {
            return null;
        }

        Path target = productUploadDir.resolve(normalizedPath.substring(PUBLIC_PREFIX.length())).normalize();
        return target.startsWith(productUploadDir) ? target : null;
    }

    private void validateImage(MultipartFile image) {
        String extension = getExtension(image.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("A imagem do produto deve estar em JPG, JPEG, PNG ou WEBP.");
        }

        String contentType = image.getContentType();
        if (!StringUtils.hasText(contentType) || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("O tipo de arquivo enviado para o produto nao e suportado.");
        }
    }

    private String getExtension(String filename) {
        String extension = StringUtils.getFilenameExtension(filename);
        if (!StringUtils.hasText(extension)) {
            throw new BusinessException("A imagem do produto precisa ter uma extensao valida.");
        }
        return extension.toLowerCase();
    }

    private void ensureUploadDirectory(Path directory) {
        try {
            Files.createDirectories(directory);
        } catch (IOException ex) {
            throw new IllegalStateException("Nao foi possivel preparar o diretorio de upload dos produtos.", ex);
        }
    }

    public record StoredProductImage(String filename, String imageUrl) {
    }
}
