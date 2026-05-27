package com.home.cloud.service;

import com.home.cloud.exception.FileException;
import com.home.cloud.model.AccountId;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TrashService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void moveObject(String sourcePath, String targetPath) {
        try {
            AccountId principal = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Integer accountId = principal.getAccount_id();
            String bucket = "account" + accountId;

            String oldFileName = sourcePath.contains("/")
                    ? sourcePath.substring(sourcePath.lastIndexOf("/") + 1)
                    : sourcePath;
            String oldFolder = sourcePath.contains("/")
                    ? sourcePath.substring(0, sourcePath.lastIndexOf("/"))
                    : "";

            String newFileName = targetPath.contains("/")
                    ? targetPath.substring(targetPath.lastIndexOf("/") + 1)
                    : targetPath;
            String newFolder = targetPath.contains("/")
                    ? targetPath.substring(0, targetPath.lastIndexOf("/"))
                    : "";

            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucket)
                            .object(targetPath)
                            .source(
                                    CopySource.builder()
                                            .bucket(bucket)
                                            .object(sourcePath)
                                            .build()
                            )
                            .build()
            );

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(sourcePath)
                            .build()
            );

            jdbcTemplate.update(
                    "UPDATE account_file SET folder_name = ?, file_name = ? WHERE file_name = ? AND folder_name = ? AND account_id = ?",
                    newFolder, newFileName, oldFileName, oldFolder, accountId
            );
        } catch (Exception e) {
            throw new FileException("Error moving file", e);
        }
    }


}