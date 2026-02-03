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

    @PostMapping("/delete/folder")
    public ResponseEntity<?> deleteFolder(@RequestBody DeleteFolderModel deleteFolderModel) {
        folderService.deleteFolder(deleteFolderModel.getFolder_name());
        return ResponseEntity.ok(Map.of("message", "Folder deleted successfully", "folder", deleteFolderModel.getFolder_name()));
    }

    @PostMapping("/list-folders")
    public List<FolderDataBaseModel> listFolders() {
        return folderService.getFolder();
    }
}