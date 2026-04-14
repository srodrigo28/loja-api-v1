package com.loja99.config;

import java.nio.file.Path;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UploadPathResolver {

    public Path resolveBaseDir(String configuredBaseDir) {
        String normalizedBaseDir = StringUtils.hasText(configuredBaseDir)
                ? configuredBaseDir.trim()
                : "assets/uploads";

        Path configuredPath = Path.of(normalizedBaseDir);
        if (configuredPath.isAbsolute()) {
            return configuredPath.normalize();
        }

        Path workingDir = Path.of("").toAbsolutePath().normalize();
        Path localCandidate = workingDir.resolve(configuredPath).normalize();
        Path apiModuleCandidate = workingDir.resolve("api").resolve(configuredPath).normalize();

        if (workingDir.getFileName() != null && "api".equalsIgnoreCase(workingDir.getFileName().toString())) {
            return localCandidate;
        }

        if (apiModuleCandidate.startsWith(workingDir) && apiModuleCandidate.getParent() != null && apiModuleCandidate.getParent().toFile().exists()) {
            return apiModuleCandidate;
        }

        if (localCandidate.getParent() != null && localCandidate.getParent().toFile().exists()) {
            return localCandidate;
        }

        return apiModuleCandidate.startsWith(workingDir) ? apiModuleCandidate : localCandidate;
    }
}
