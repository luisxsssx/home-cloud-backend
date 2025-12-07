package com.home.cloud.controller;

import com.home.cloud.model.FolderModel;
import com.home.cloud.service.FileService;
import com.home.cloud.service.FolderService;
import io.minio.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/files")
public class Controller {

    private final FileService fileService;
    private final FolderService folderService;

    public Controller(FileService fileService, FolderService folderService) {
        this.fileService = fileService;
        this.folderService = folderService;
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("bucketName") String bucketName
    ) {
        return fileService.upFile(file, bucketName);
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        return fileService.listFiles();
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String filename) {
        return fileService.downloadFile(filename);
    }

    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        return fileService.deleteFile(filename);
    }

    @PostMapping("/create/bucket")
    public ResponseEntity<String> createBucket(@PathVariable String bucketName) {
        fileService.createBucket(bucketName);
        return ResponseEntity.ok("Bucket created successfully: " + bucketName);
    }

    // Make folder
    @PostMapping("/create/folder")
    public ResponseEntity<String> makeFolder(@RequestBody FolderModel folderModel) {
        folderService.makeFolder(folderModel);
        return ResponseEntity.ok("Folder created successfully: " + folderModel);
    }
}