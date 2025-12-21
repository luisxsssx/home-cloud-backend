package com.home.cloud.controller;

import com.home.cloud.model.AccountBucket;
import com.home.cloud.model.AccountFolder;
import com.home.cloud.service.AccountService;
import com.home.cloud.service.BucketService;
import com.home.cloud.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cloud")
public class FolderController {
    private final FolderService folderService;
    private final BucketService bucketService;
    private final AccountService accountService;

    public FolderController(FolderService folderService, BucketService bucketService, AccountService accountService) {
        this.folderService = folderService;
        this.bucketService = bucketService;
        this.accountService = accountService;
    }

    // Make bucket
    @PostMapping("/create/bucket")
    public ResponseEntity<String> createBucket(@RequestBody AccountBucket accountBucket) {
        bucketService.createBucket(accountBucket.getBucket_name());
        accountService.saveBucketAccount(accountBucket);
        return ResponseEntity.ok("Bucket created successfully: " + accountBucket.getBucket_name());
    }

    // Make folder
    @PostMapping("/{bucketName}/create/folder")
    public ResponseEntity<String> makeFolder(@PathVariable String bucketName, @RequestBody AccountFolder accountFolder) {
        folderService.makeFolder(bucketName, accountFolder.getFolder_name());
        accountService.saveBucketFolder(accountFolder.getFolder_name(), accountFolder.getAccount_id(), accountFolder.getBucket_id());
        return ResponseEntity.ok("Folder created successfully: " + accountFolder.getFolder_name());
    }
}