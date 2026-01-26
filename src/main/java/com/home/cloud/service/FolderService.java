package com.home.cloud.service;

import com.home.cloud.exception.FileException;
import com.home.cloud.exception.folder.FolderException;
import com.home.cloud.model.AccountId;
import com.home.cloud.model.FolderDataBaseModel;
import io.minio.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public void makeFolder( String folder_name) {

        AccountId principal = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Integer accountId = principal.getAccount_id();

        String bucket_name = "account" + accountId;

        var emptyStream = new ByteArrayInputStream(new byte[] {});
        if (!bucketService.isBucketExists(bucket_name)) {
            throw new IllegalArgumentException("Bucket not found: " + bucket_name);
        }
        try {
            AccountId id = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Integer account_id = id.getAccount_id();
            jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                CallableStatement cs = connection.prepareCall("call sp_account_folder(?,?)");
                cs.setString(1, folder_name);
                cs.setInt(2, account_id);
                cs.execute();
                return null;
            });
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket_name)
                            .stream(emptyStream, 0, -1)
                            .object(folder_name.endsWith("/") ? folder_name : folder_name + "/")
                            .build()
            );
        } catch (Exception e) {
            throw new FolderException("I cannot create the folder: " + folder_name, e);
        }
    }

    public void deleteFolder(Integer folder_id, String folder_name) {
        AccountId principal = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer accountId = principal.getAccount_id();
        String bucket_name = "account" + accountId;

        try {
            jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                CallableStatement cs = connection.prepareCall("call sp_delete_folder(?)");
                cs.setInt(1, folder_id);
                cs.execute();
                return null;
            } );

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

    // Get folder from database
    public List<FolderDataBaseModel> getFolder() {
        try {
            AccountId id = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Integer account_id = id.getAccount_id();
            return jdbcTemplate.query(
                    "SELECT * FROM f_get_folder(?)",
                    new Object[]{ account_id },
                    (rs, rowNum) -> {
                        FolderDataBaseModel folder = new FolderDataBaseModel();
                        folder.setFolder_id(rs.getInt("out_folder_id"));
                        folder.setFolder_name(rs.getString("out_folder_name"));
                        folder.setAccount_id(rs.getInt("out_account_id"));
                        folder.setBucket_id(rs.getInt("out_bucket_id"));
                        return folder;
                    }
            );
        } catch (Exception e) {
            throw new FolderException("Error getting data", e);
        }
    }
}