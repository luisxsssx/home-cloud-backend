package com.home.cloud.controller;

import com.home.cloud.model.AccountBucket;
import com.home.cloud.model.AccountFolder;
import com.home.cloud.model.DeleteFolderModel;
import com.home.cloud.service.BucketService;
import com.home.cloud.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cloud/folder")
public class FolderController {
    private final FolderService folderService;
    private final BucketService bucketService;

    public FolderController(FolderService folderService,
                            BucketService bucketService) {
        this.folderService = folderService;
        this.bucketService = bucketService;
    }

    // Make bucket
    @PostMapping("/create/bucket")
    public ResponseEntity<String> createBucket(@RequestBody AccountBucket accountBucket) {
        bucketService.createBucket(accountBucket.getBucket_name(), accountBucket.getAccount_id());
        return ResponseEntity.ok("Bucket created successfully: " + accountBucket.getBucket_name());
    }

    // Make folder
    @PostMapping("/{bucketName}/create/folder")
    public ResponseEntity<String> makeFolder(@PathVariable String bucketName,
                                             @RequestBody AccountFolder accountFolder) {
        folderService.makeFolder(bucketName, accountFolder.getFolder_name(),
                accountFolder.getAccount_id(), accountFolder.getBucket_id());
        return ResponseEntity.ok("Folder created successfully: " + accountFolder.getFolder_name());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFolder(@RequestBody DeleteFolderModel deleteFolderModel) {
        folderService.deleteFolder(deleteFolderModel.getFolder_id(),
                deleteFolderModel.getBucket_name(), deleteFolderModel.getFolder_name());
        return ResponseEntity.ok("Folder deleted successfully: " + " " + deleteFolderModel.getFolder_name());
    }
}