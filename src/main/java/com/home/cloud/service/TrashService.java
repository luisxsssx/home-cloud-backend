package com.home.cloud.service;

import com.home.cloud.model.AccountId;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TrashService {

    @Autowired
    private MinioClient minioClient;

    public void moveObject(String sourcePath, String targetPath) throws Exception {
        AccountId principal = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer accountId = principal.getAccount_id();
        String bucket = "account" + accountId;
        // Copy the object to the new folder path
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(bucket)
                        .object(targetPath) // New folder/path
                        .source(
                                CopySource.builder()
                                        .bucket(bucket)
                                        .object(sourcePath) // Original folder/path
                                        .build()
                        )
                        .build()
        );

        // Remove the original object
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(sourcePath)
                        .build()
        );
    }


}