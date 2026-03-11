package com.example.demo.product_category.common.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootPath;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) throws IOException {
        this.rootPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(rootPath);
    }

    public String store(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File upload không hợp lệ");
        }
        try {
            Path folder = rootPath.resolve(subFolder).normalize();
            Files.createDirectories(folder);

            String original = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = "";
            int index = original.lastIndexOf('.');
            if (index >= 0) {
                extension = original.substring(index);
            }
            String fileName = UUID.randomUUID() + extension;
            Path target = folder.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/" + rootPath.getFileName() + "/" + subFolder + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Không lưu được file: " + e.getMessage(), e);
        }
    }

    public void deleteByRelativeUrl(String relativeUrl) {
        if (relativeUrl == null || relativeUrl.isBlank()) {
            return;
        }
        try {
            String cleaned = relativeUrl.startsWith("/") ? relativeUrl.substring(1) : relativeUrl;
            Path filePath = Paths.get(cleaned).toAbsolutePath();
            if (!filePath.startsWith(rootPath)) {
                filePath = rootPath.getParent() != null ? rootPath.getParent().resolve(cleaned).normalize() : rootPath.resolve(cleaned).normalize();
            }
            Files.deleteIfExists(filePath);
        } catch (Exception ignored) {
        }
    }
}
