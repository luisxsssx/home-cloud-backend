package com.home.cloud.service;

import com.home.cloud.exception.FileException;
import com.home.cloud.model.FolderModel;
import io.minio.*;
import io.minio.messages.Item;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    private final MinioClient minioClient;

    public FileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void upFile(MultipartFile file, String bucketName, FolderModel folderModel) {
        try {
            String fileName = file.getOriginalFilename();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(folderModel.getFolderName() + file)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new FileException("I cannot upload file", e);
        }
    }

    public List<String> listFiles(String bucketName) {
        try {
            List<String> filesNames = new ArrayList<>();
            Iterable<Result<Item>> items = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).build()
            );
            for (Result<Item> item : items) {
                filesNames.add(item.get().objectName());
            }
            return filesNames;
        } catch (Exception e) {
            throw new FileException("List" + bucketName, e);
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