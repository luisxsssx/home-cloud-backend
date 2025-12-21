package com.home.cloud.controller;

import com.home.cloud.model.FileModel;
import com.home.cloud.service.AccountService;
import com.home.cloud.service.BucketService;
import com.home.cloud.service.FileService;
import io.minio.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("cloud/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") FileModel fileModel
    ) throws Exception {
        String objectKey = fileService.upFile(file, fileModel.getBucket_name(), fileModel.getFolder_name(), fileModel.getAccount_id(), fileModel.getBucket_id());
        return ResponseEntity.ok(objectKey);
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles(@RequestParam String bucket_name,
                                                  @RequestParam(required = false) String folder_name) {
       List<String> files = fileService.listFiles(bucket_name, folder_name);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String filename, @RequestParam String bucketName) {
        InputStreamResource resource = fileService.downloadFile(filename, bucketName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename, @RequestParam String bucketName) {
        fileService.deleteFile(filename, bucketName);
        return ResponseEntity.ok("File successfully deleted" + filename);
    }
}