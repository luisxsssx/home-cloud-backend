package com.home.cloud.service;

import com.home.cloud.exception.FileEliminationException;
import com.home.cloud.exception.FileException;
import com.home.cloud.jwt.JwtUtil;
import com.home.cloud.model.*;
import io.minio.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private JwtUtil jwtUtil;

    private final MinioClient minioClient;

    public FileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // Save the file data to the database and upload the file to Minio
    public String upFile(
            MultipartFile file,
            String folder_name) {
        AccountId principal = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BucketId bucketId = (BucketId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer accountId = principal.getAccount_id();
        Integer bucket_id = bucketId.getBucket_id();
        String bucket_name = "account" + accountId;

        try {
            AccountId id = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String file_name = file.getOriginalFilename();
            String file_size = String.valueOf(file.getSize());
            Integer account_id = id.getAccount_id();
            jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                CallableStatement cs = connection.prepareCall("call sp_account_file(?,?,?,?,?)");
                cs.setString(1, folder_name);
                cs.setString(2, file_name);
                cs.setInt(3, account_id);
                cs.setInt(4, bucket_id);
                cs.setString(5, file_size);
                cs.execute();
                return null;
            });


            String fileName = file.getOriginalFilename();
            String objectName;

            if(folder_name != null) {
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

    public List<String> listFolders() throws Exception {
        AccountId principal = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer accountId = principal.getAccount_id();
        String bucket_name = "account" + accountId;

        List<String> folder = new ArrayList<>();
        Iterable<Result<Item>> results =
                minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(bucket_name)
                                .recursive(true)
                                .prefix("")
                                .build()
                );
        for (Result<Item> r : results) {
            Item item = r.get();
            folder.add(item.objectName());

        }

        StringBuilder sb = new StringBuilder(folder.toString());

        int start = sb.indexOf("/");

        if (start != -1) {
            sb.delete(start, sb.length());
        }

        String s = sb.toString().trim();

        return folder;
    }

    public List<FolderResponse> listRoot(String folder_name) {
        AccountId principal = (AccountId) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Integer accountId = principal.getAccount_id();
        String bucket_name = "account" + accountId;

        List<FolderResponse> elements = new ArrayList<>();

        if (!folder_name.isEmpty() && !folder_name.endsWith("/")) {
            folder_name = folder_name + "/";
        }

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucket_name)
                        .prefix(folder_name)
                        .delimiter("/")
                        .recursive(false)
                        .build()
        );

        for (Result<Item> result : results) {
            try {
                Item item = result.get();
                String fullName = item.objectName();

                if (!folder_name.isEmpty() && fullName.equals(folder_name)) {
                    continue;
                }

                long size = item.size();
                String n = fullName.substring(folder_name.length());

                if (item.isDir()) {
                    n = n.substring(0, n.length() - 1);
                    if (!n.isEmpty()) {
                        elements.add(new FolderResponse(n + "/", size));
                    }
                } else {
                    elements.add(new FolderResponse(n, size));
                }

            } catch (Exception e) {
                throw new RuntimeException("Error " + folder_name, e);
            }
        }

        return elements;
    }


    public InputStreamResource downloadFile(String filename) {
        AccountId principal = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer accountId = principal.getAccount_id();
        String bucket_name = "account" + accountId;

        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket_name)
                            .object(filename)
                            .build()
            );
           return new InputStreamResource(stream);
        } catch (Exception e) {
            throw new FileException("Download successfully", e);
        }
    }

    public void renameFile(FileRenameModel fileRenameModel) {
        AccountId principal = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer accountId = principal.getAccount_id();
        String bucket_name = "account" + accountId;
        try {
            switch (fileRenameModel.getItemType()) {
                case FILE -> {
                    jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                        CallableStatement cs = connection.prepareCall("call sp_update_file_name(?,?)");
                        cs.setString(1, fileRenameModel.getNew_file_name());
                        cs.setString(2, fileRenameModel.getOld_file_name());
                        cs.execute();
                        return null;
                    } );
                }

                case FOLDER -> {
                    jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                        CallableStatement cs = connection.prepareCall("call sp_update_folder_name(?,?)");
                        cs.setString(1, fileRenameModel.getNew_file_name());
                        cs.setString(2, fileRenameModel.getOld_file_name());
                        cs.execute();
                        return null;
                    } );
                }
            }

            minioClient.copyObject(
                    CopyObjectArgs
                            .builder()
                            .bucket(bucket_name)
                            .object(fileRenameModel.getNew_file_name())
                            .source(
                                    CopySource.builder()
                                            .bucket(bucket_name)
                                            .object(fileRenameModel.getOld_file_name())
                                            .build()
                            )
                            .build()
            );

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket_name)
                            .object(fileRenameModel.getOld_file_name())
                            .build()
            );
        } catch (Exception e) {
            throw new FileEliminationException("File removed successfully", e);
        }
    }

    public void deleteItem(DeleteItemModel deleteItemModel) {
        AccountId principal = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer accountId = principal.getAccount_id();
        String bucket_name = "account" + accountId;
        try {
            switch (deleteItemModel.getItemType()) {
                case FILE -> {
                    jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                        CallableStatement cs = connection.prepareCall("call sp_delete_file(?)");
                        cs.setString(1, deleteItemModel.getName());
                        cs.execute();
                        return null;
                    } );

                    minioClient.removeObject(
                            RemoveObjectArgs.builder().bucket(bucket_name).object(deleteItemModel.getName()).build()
                    );
                }

                case FOLDER -> {
                    jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                        CallableStatement cs = connection.prepareCall("call sp_delete_folder(?)");
                        cs.setString(1, deleteItemModel.getName());
                        cs.execute();
                        return null;
                    } );

                    List<String> result = new ArrayList<>();
                    Iterable<Result<Item>> items = minioClient.listObjects(
                            ListObjectsArgs.builder()
                                    .bucket(bucket_name)
                                    .prefix(deleteItemModel.getName())
                                    .build());
                    for (Result<Item> item : items) {
                        minioClient.removeObject(RemoveObjectArgs.builder()
                                .bucket(bucket_name)
                                .object(item.get().objectName())
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String file_name) {
        AccountId principal = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer accountId = principal.getAccount_id();
        String bucket_name = "account" + accountId;
        try {
            jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                CallableStatement cs = connection.prepareCall("call sp_delete_file(?)");
                cs.setString(1, file_name);
                cs.execute();
                return null;
            } );

            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucket_name).object(file_name).build()
            );
        } catch (Exception e) {
            throw new FileException("File deleted successfully", e);
        }
    }
}