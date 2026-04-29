package com.home.cloud.model;

import com.home.cloud.model.type.FileStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileModel {
    private String bucket_name;
    private String folder_name;
    //private Integer account_id;
    private Integer bucket_id;
    private FileStatus fileStatus;
}