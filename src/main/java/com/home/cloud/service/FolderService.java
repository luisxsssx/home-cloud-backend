package com.home.cloud.service;

import com.home.cloud.exception.folder.FolderException;
import com.home.cloud.model.FolderModel;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class FolderService {
    private final MinioClient minioClient;
    private final BucketService bucketService;

    public FolderService(MinioClient minioClient, BucketService bucketService) {
        this.minioClient = minioClient;
        this.bucketService = bucketService;
    }

    // Make folder
    public void makeFolder(String bucketName, String folder_name) {
        var emptyStream = new ByteArrayInputStream(new byte[] {});

        if (!bucketService.isBucketExists(bucketName)) {
            throw new IllegalArgumentException("Bucket not found: " + bucketName);
        }

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .stream(emptyStream, 0, -1)
                            .object(folder_name.endsWith("/") ? folder_name : folder_name + "/")
                            .build()
            );
        } catch (Exception e) {
            throw new FolderException("I cannot create the folder: " + folder_name, e);
        }
    }
}