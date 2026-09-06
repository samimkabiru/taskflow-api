package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.exceptions.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
public class FileStorageService {

    private final S3Client s3Client;
    private final String bucket;

    public FileStorageService(
            @Value("${app.storage.access-key}") String accessKey,
            @Value("${app.storage.secret-key}") String secretKey,
            @Value("${app.storage.endpoint}") String endpoint,
            @Value("${app.storage.bucket}") String bucket,
            @Value("${app.storage.region}") String region
    ) {
        this.bucket = bucket;
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String store(MultipartFile file, String prefix) {
        var originalFilename = file.getOriginalFilename();
        var safeFilename = originalFilename != null
                ? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_")
                : "file";
        var storageKey = prefix + "/" + UUID.randomUUID() + "_" + safeFilename;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(storageKey)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
            return storageKey;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file", e);
        }
    }

    public byte[] load(String storageKey) {
        try {
            return s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(storageKey)
                            .build()
            ).asByteArray();
        } catch (Exception e) {
            throw new FileStorageException("Failed to load file", e);
        }
    }

    public void delete(String storageKey) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(storageKey)
                            .build()
            );
        } catch (Exception e) {
            throw new FileStorageException("Failed to delete file", e);
        }
    }
}