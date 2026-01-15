package com.home.cloud.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FolderModel {
    private Integer folder_id;
    private String folder_name;
    private String bucket_name;
}