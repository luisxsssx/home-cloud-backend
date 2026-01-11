package com.home.cloud.controller;

import com.home.cloud.model.AccountBucket;
import com.home.cloud.model.BucketModel;
import com.home.cloud.service.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bucket")
public class BucketController {
    @Autowired
    private BucketService bucketService;

    // Make bucket
    @PostMapping("/create/bucket")
    public ResponseEntity<String> createBucket(@RequestBody AccountBucket accountBucket) {
        bucketService.createBucket(accountBucket.getUsername());
        return ResponseEntity.ok("Bucket created successfully: " + accountBucket.getBucket_name());
    }

    @PostMapping("/id")
    public List<BucketModel> getBucketForId() {
        return bucketService.getBucket();
    }
}