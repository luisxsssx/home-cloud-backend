package com.home.cloud.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrashModel {
    private String sourcePath;
    private String targetPath;
}