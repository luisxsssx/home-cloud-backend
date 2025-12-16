package com.home.cloud.controller;

import com.home.cloud.model.BucketModel;
import com.home.cloud.model.FolderModel;
import com.home.cloud.service.BucketService;
import com.home.cloud.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cloud")
public class FolderController {
    private final FolderService folderService;
    private final BucketService bucketService;

    public FolderController(FolderService folderService, BucketService bucketService) {
        this.folderService = folderService;
        this.bucketService = bucketService;
    }

    // Make bucket
    @PostMapping("/create/bucket")
    public ResponseEntity<String> createBucket(@RequestBody BucketModel bucketModel) {
        bucketService.createBucket(bucketModel);
        return ResponseEntity.ok("Bucket created successfully: " + bucketModel.getBucketName());
    }

    // Make folder
    @PostMapping("/{bucketName}/create/folder")
    public ResponseEntity<String> makeFolder(@PathVariable String bucketName, @RequestBody FolderModel folderModel) {
        folderService.makeFolder(bucketName, folderModel);
        return ResponseEntity.ok("Folder created successfully: " + folderModel.getFolderName());
    }
}