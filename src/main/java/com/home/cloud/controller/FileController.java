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

@RestController
@RequestMapping("cloud/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") FileModel fileModel
    ) throws Exception {
        String objectKey = fileService.upFile(file, fileModel.getBucket_name(),
                fileModel.getFolder_name(),
                fileModel.getBucket_id());
        return ResponseEntity.ok(objectKey);
    }

    @PostMapping("/list")
    public List<FolderResponse> listRoot(@RequestBody DataModel dataModel) {
        return fileService.listRoot(dataModel.getBucket_name(),
                dataModel.getFolder_name());
    }

    @GetMapping("/list/dir")
    public ResponseEntity<List<String>> listFolders(@RequestParam String bucket_name) {
        try {
            List<String> files = fileService.listFolders(bucket_name);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String filename,
                                                            @RequestParam String bucketName) {
        InputStreamResource resource = fileService.downloadFile(filename, bucketName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/rename/file")
    public ResponseEntity<String> renameFile(@RequestBody FileRenameModel fileRenameModel) {
        fileService.renameFile(fileRenameModel.getBucket_name(),
                fileRenameModel.getNew_file_name(),
                fileRenameModel.getOld_file_name());
        return ResponseEntity.ok("File renamed successfully");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestBody DeleteFileModel deleteFileModel) {
        fileService.deleteFile(deleteFileModel.getFile_name(), deleteFileModel.getBucket_name());
        return ResponseEntity.ok("File successfully deleted" + " " + deleteFileModel.getFile_name());
    }
}