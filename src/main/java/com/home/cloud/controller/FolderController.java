package com.home.cloud.controller;

import com.home.cloud.model.FolderModel;
import com.home.cloud.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cloud")
public class FolderController {
    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    // Make folder
    @PostMapping("/{bucketName}/create/folder")
    public ResponseEntity<String> makeFolder(@PathVariable String bucketName, @RequestBody FolderModel folderModel) {
        folderService.makeFolder(bucketName, folderModel);
        return ResponseEntity.ok("Folder created successfully: " + folderModel.getFolderName());
    }
}