package com.home.cloud.service;

import com.home.cloud.exception.FolderCreationException;
import com.home.cloud.model.BucketModel;
import com.home.cloud.model.FolderModel;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class FolderService {
    private final MinioClient minioClient;

    public FolderService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // Make folder
    public void makeFolder(FolderModel folderModel) {
        var emptyStream = new ByteArrayInputStream(new byte[] {});

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(folderModel.getBucketName())
                            .object(folderModel.getFolderName().endsWith("/") ? folderModel.getFolderName() : folderModel.getFolderName() + "/")
                            .stream(emptyStream, 0, -1)
                            .build()
            );
        } catch (Exception e) {
            throw new FolderCreationException("I cannot create the folder: " + folderModel.getFolderName(), e);
        }
    }
}