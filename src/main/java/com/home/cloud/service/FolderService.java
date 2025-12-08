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

    public FolderService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // Make folder
    public void makeFolder(String bucketName, FolderModel folderModel) {
        var emptyStream = new ByteArrayInputStream(new byte[] {});

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .stream(emptyStream, 0, -1)
                            .object(folderModel.getFolderName().endsWith("/") ? folderModel.getFolderName() : folderModel.getFolderName() + "/")
                            .build()
            );
        } catch (Exception e) {
            throw new FolderException("I cannot create the folder: " + folderModel.getFolderName(), e);
        }
    }
}