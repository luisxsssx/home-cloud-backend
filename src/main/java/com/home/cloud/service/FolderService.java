package com.home.cloud.service;

import com.home.cloud.exception.FileException;
import com.home.cloud.exception.folder.FolderException;
import io.minio.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.List;

@Service
public class FolderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final MinioClient minioClient;
    private final BucketService bucketService;

    public FolderService(MinioClient minioClient, BucketService bucketService) {
        this.minioClient = minioClient;
        this.bucketService = bucketService;
    }

    // Make folder
    public void makeFolder(String bucketName, String folder_name, Integer account_id, Integer bucket_id) {
        var emptyStream = new ByteArrayInputStream(new byte[] {});

        if (!bucketService.isBucketExists(bucketName)) {
            throw new IllegalArgumentException("Bucket not found: " + bucketName);
        }

        try {
            jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                CallableStatement cs = connection.prepareCall("call sp_account_folder(?,?,?)");

                cs.setString(1, folder_name);
                cs.setInt(2, account_id);
                cs.setInt(3, bucket_id);
                cs.execute();
                return null;
            });
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

    public void deleteFolder(String bucket_name, String folder_name) {
        try {
            List<String> result = new ArrayList<>();
            Iterable<Result<Item>> items = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket_name)
                            .prefix(folder_name)
                            .build());
            for (Result<Item> item : items) {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucket_name)
                        .object(item.get().objectName())
                        .build());
            }
        } catch (Exception e) {
            throw new FileException("List" + bucket_name, e);
        }
    }
}