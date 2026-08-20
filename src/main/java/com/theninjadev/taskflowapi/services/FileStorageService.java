package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.exceptions.FileStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${app.upload-dir}")
    private String uploadDir;

    public String store(MultipartFile file) {
        var originalFilename = file.getOriginalFilename();
        var safeFilename = originalFilename != null
                ? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_")
                : "file";
        var storageKey = UUID.randomUUID() + "_" + safeFilename;

        try {
            var uploadPath = Path.of(uploadDir);
            Files.createDirectories(uploadPath);

            var destination = uploadPath.resolve(storageKey);
            file.transferTo(destination);

            return storageKey;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file", e);
        }
    }

    public byte[] load(String storageKey) {
        var destination = Path.of(uploadDir).resolve(storageKey);

        try {
            return Files.readAllBytes(destination);
        } catch (IOException e) {
            throw new FileStorageException("Failed to load file", e);
        }
    }
}
