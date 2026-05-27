package com.home.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileItemResponse {
    private String fileName;
    private String folderName;
    private Long fileSize;
    private Timestamp createdAt;
}
