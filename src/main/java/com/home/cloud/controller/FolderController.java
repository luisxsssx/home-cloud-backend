package com.home.cloud.controller;

import com.home.cloud.model.AccountBucket;
import com.home.cloud.model.DeleteFolderModel;
import com.home.cloud.model.FolderDataBaseModel;
import com.home.cloud.model.FolderModel;
import com.home.cloud.service.BucketService;
import com.home.cloud.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cloud")
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
    @PostMapping("/create/folder")
    public ResponseEntity<?> makeFolder(@RequestBody FolderModel folderModel) {
        folderService.makeFolder(folderModel.getBucket_name(), folderModel.getFolder_name(),
                folderModel.getAccount_id(), folderModel.getBucket_id());
        return ResponseEntity.ok(Map.of("message", "Folder created successfully", "folder", folderModel.getFolder_name()));
    }

    @DeleteMapping("/delete/folder")
    public ResponseEntity<String> deleteFolder(@RequestBody DeleteFolderModel deleteFolderModel) {
        folderService.deleteFolder(deleteFolderModel.getFolder_id(),
                deleteFolderModel.getBucket_name(), deleteFolderModel.getFolder_name());
        return ResponseEntity.ok("Folder deleted successfully: " + " " + deleteFolderModel.getFolder_name());
    }

    @PostMapping("/list-folders")
    public List<FolderDataBaseModel> listFolders(@RequestBody FolderDataBaseModel folderDataBaseModel) {
        return folderService.getFolder(folderDataBaseModel.getAccount_id());
    }

}