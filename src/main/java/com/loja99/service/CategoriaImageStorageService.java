package com.loja99.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.loja99.config.UploadPathResolver;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.loja99.exception.BusinessException;

@Service
public class CategoriaImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );
    private static final String PUBLIC_PREFIX = "/uploads/categorias/";

    private final Path categoryUploadDir;

    public CategoriaImageStorageService(
            @Value("${app.upload.base-dir:assets/uploads}") String uploadBaseDir,
            UploadPathResolver uploadPathResolver
    ) {
        this.categoryUploadDir = uploadPathResolver.resolveBaseDir(uploadBaseDir).resolve("categorias");
        ensureUploadDirectory();
    }

    public String storeRequiredImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BusinessException("A imagem da categoria e obrigatoria.");
        }
        return storeImage(image);
    }

    public String replaceImage(String currentImagePath, MultipartFile image) {
        String newImagePath = storeRequiredImage(image);
        deleteImage(currentImagePath);
        return newImagePath;
    }

    public void deleteImage(String imagePath) {
        if (!StringUtils.hasText(imagePath)) {
            return;
        }

        String normalizedPath = imagePath.trim().replace('\\', '/');

        if (!normalizedPath.startsWith(PUBLIC_PREFIX)) {
            return;
        }

        Path target = categoryUploadDir.resolve(normalizedPath.substring(PUBLIC_PREFIX.length())).normalize();

        if (!target.startsWith(categoryUploadDir)) {
            return;
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            throw new IllegalStateException("Nao foi possivel remover a imagem antiga da categoria.", ex);
        }
    }

    private String storeImage(MultipartFile image) {
        validateImage(image);
        ensureUploadDirectory();

        String extension = getExtension(image.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;
        Path target = categoryUploadDir.resolve(filename).normalize();

        if (!target.startsWith(categoryUploadDir)) {
            throw new IllegalStateException("Caminho de upload invalido para categoria.");
        }

        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Nao foi possivel salvar a imagem da categoria.", ex);
        }

        return PUBLIC_PREFIX + filename;
    }

    private void validateImage(MultipartFile image) {
        String extension = getExtension(image.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("A imagem da categoria deve estar em JPG, JPEG, PNG ou WEBP.");
        }

        String contentType = image.getContentType();
        if (!StringUtils.hasText(contentType) || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("O tipo de arquivo enviado para a categoria nao e suportado.");
        }
    }

    private String getExtension(String filename) {
        String extension = StringUtils.getFilenameExtension(filename);
        if (!StringUtils.hasText(extension)) {
            throw new BusinessException("A imagem da categoria precisa ter uma extensao valida.");
        }
        return extension.toLowerCase();
    }

    private void ensureUploadDirectory() {
        try {
            Files.createDirectories(categoryUploadDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Nao foi possivel preparar o diretorio de upload das categorias.", ex);
        }
    }
}
