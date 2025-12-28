package com.home.cloud.service;

import com.home.cloud.exception.FileEliminationException;
import com.home.cloud.exception.FileException;
import io.minio.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final MinioClient minioClient;

    public FileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // Save the file data to the database and upload the file to Minio
    public String upFile(
            MultipartFile file,
            String bucket_name,
            String folder_name,
            Integer account_id,
            Integer bucket_id) {
        try {
            String file_name = file.getOriginalFilename();
            jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                CallableStatement cs = connection.prepareCall("call sp_account_file(?,?,?,?)");

                cs.setString(1, folder_name);
                cs.setString(2, file_name);
                cs.setInt(3, account_id);
                cs.setInt(4, bucket_id);
                cs.execute();
                return null;
            } );

            String fileName = file.getOriginalFilename();
            String objectName;

            if(folder_name != null && folder_name != null) {
                objectName = folder_name + "/" + fileName;
            } else {
                objectName = fileName;
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket_name)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return bucket_name + "/" + objectName;
        } catch (Exception e) {
            throw new FileException("I cannot upload file", e);
        }
    }

    public List<String> listFiles(String bucket_name, String folder_name) {
        try {
            List<String> filesNames = new ArrayList<>();
            Iterable<Result<Item>> items = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket_name)
                            .prefix(folder_name)
                            .recursive(true)
                            .build());
            for (Result<Item> item : items) {
                filesNames.add(item.get().objectName());
            }
            return filesNames;
        } catch (Exception e) {
            throw new FileException("List" + bucket_name, e);
        }
    }

    public InputStreamResource downloadFile(String filename, String bucketName) {
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );
           return new InputStreamResource(stream);
        } catch (Exception e) {
            throw new FileException("Download successfully", e);
        }
    }

    public void renameFile(String bucket_name, String new_file_name, String old_file_name) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs
                            .builder()
                            .bucket(bucket_name)
                            .object(new_file_name)
                            .source(
                                    CopySource.builder()
                                            .bucket(bucket_name)
                                            .object(old_file_name)
                                            .build()
                            )
                            .build()
            );

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket_name)
                            .object(old_file_name)
                            .build()
            );
        } catch (Exception e) {
            throw new FileEliminationException("File removed successfully", e);
        }
    }

    public void deleteFile(String filename, String bucketName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(filename).build()
            );
        } catch (Exception e) {
            throw new FileException("File deleted successfully", e);
        }
    }
}