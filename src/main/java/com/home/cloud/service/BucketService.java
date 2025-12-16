package com.home.cloud.service;

import com.home.cloud.exception.BucketCreationException;
import com.home.cloud.exception.BucketNotFoundException;
import com.home.cloud.model.BucketModel;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.stereotype.Service;

@Service
public class BucketService {

    private final MinioClient minioClient;

    public BucketService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createBucket(BucketModel bucketModel) {
        if (!isBucketNameValid(bucketModel.getBucketName())) {
            throw new IllegalArgumentException("Invalid bucket name: " + bucketModel.getBucketName());
        }
        try {
            minioClient.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(bucketModel.getBucketName())
                            .build()
            );
        } catch (Exception e) {
            throw new BucketCreationException("I cannot create the bucket", e);
        }
    }

    private boolean isBucketNameValid(String bucketName) {
        return bucketName.length() >= 3 && bucketName.length() <= 63 &&
                bucketName.matches("^[a-z0-9.-]+$") &&
                !bucketName.contains("..") &&
                !bucketName.startsWith("-") &&
                !bucketName.endsWith("-");
    }

    public boolean isBucketExists(String bucketName) {
        try {
            minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build());
            return true;
        } catch (Exception e) {
            throw new BucketNotFoundException("Bucket not found: " + e);
        }
    }

}