package com.home.cloud.controller;

import com.home.cloud.model.FolderModel;
import com.home.cloud.service.BucketService;
import com.home.cloud.service.FileService;
import io.minio.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/files")
public class FileController {

    private final FileService fileService;
    private final BucketService bucketService;

    public FileController(FileService fileService, BucketService bucketService) {
        this.fileService = fileService;
        this.bucketService = bucketService;
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("bucketName") String bucketName,
            @RequestParam("folderName")FolderModel folderModel
    ) {
        fileService.upFile(file, bucketName, folderModel);
        return ResponseEntity.ok("Files uploaded successfully");
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles(@RequestParam String bucketName) {
       List<String> files = fileService.listFiles(bucketName);
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