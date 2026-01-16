package com.home.cloud.controller;

import com.home.cloud.model.*;
import com.home.cloud.service.FileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("cloud/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") FileModel fileModel
    ) throws Exception {
        String objectKey = fileService.upFile(file,
                fileModel.getFolder_name(),
                fileModel.getBucket_id());
        return ResponseEntity.ok(Map.of("message", "File uploaded successfully: " + " " + file.getName()));
    }

    @PostMapping("/list")
    public List<FolderResponse> listRoot(@RequestBody DataModel dataModel) {
        return fileService.listRoot(dataModel.getFolder_name());
    }

    @GetMapping("/list/dir")
    public ResponseEntity<List<String>> listFolders() {
        try {
            List<String> files = fileService.listFolders();
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String filename) {
        InputStreamResource resource = fileService.downloadFile(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/rename/file")
    public ResponseEntity<String> renameFile(@RequestBody FileRenameModel fileRenameModel) {
        fileService.renameFile(
                fileRenameModel.getNew_file_name(),
                fileRenameModel.getOld_file_name());
        return ResponseEntity.ok("File renamed successfully");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestBody DeleteFileModel deleteFileModel) {
        fileService.deleteFile(deleteFileModel.getFile_name());
        return ResponseEntity.ok("File successfully deleted" + " " + deleteFileModel.getFile_name());
    }
}