package com.home.cloud.controller;

import com.home.cloud.model.DeleteFolderModel;
import com.home.cloud.model.FolderDataBaseModel;
import com.home.cloud.model.FolderModel;
import com.home.cloud.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cloud")
public class FolderController {
    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    // Make folder
    @PostMapping("/create/folder")
    public ResponseEntity<?> makeFolder(@RequestBody FolderModel folderModel) {
        folderService.makeFolder(folderModel.getFolder_name());
        return ResponseEntity.ok(Map.of("message", "Folder created successfully", "folder", folderModel.getFolder_name()));
    }

    @DeleteMapping("/delete/folder")
    public ResponseEntity<String> deleteFolder(@RequestBody DeleteFolderModel deleteFolderModel) {
        folderService.deleteFolder(deleteFolderModel.getFolder_id(),
                 deleteFolderModel.getFolder_name());
        return ResponseEntity.ok("Folder deleted successfully: " + " " + deleteFolderModel.getFolder_name());
    }

    @PostMapping("/list-folders")
    public List<FolderDataBaseModel> listFolders() {
        return folderService.getFolder();
    }
}