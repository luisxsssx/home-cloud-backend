package com.home.cloud.service;

import com.home.cloud.exception.FolderEliminationException;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.stereotype.Service;

@Service
public class BucketService {

    private final MinioClient minioClient;

    public BucketService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createBucket(String name) {
        try {
            minioClient.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(name)
                            .build()
            );
        } catch (Exception e) {
            throw new FolderEliminationException("I cannot create the bucket", e);
        }
    }
}