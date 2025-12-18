package com.home.cloud.service;

import com.home.cloud.exception.BucketCreationException;
import com.home.cloud.exception.BucketNotFoundException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.springframework.stereotype.Service;

@Service
public class BucketService {

    private final MinioClient minioClient;

    public BucketService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createBucket(@MonotonicNonNull String bucketName) {
        if (!isBucketNameValid(bucketName)) {
            throw new IllegalArgumentException("Invalid bucket name: " + bucketName);
        }
        try {
            minioClient.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(bucketName)
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