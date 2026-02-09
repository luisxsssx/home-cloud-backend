package com.home.cloud.model;

import com.home.cloud.model.type.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileRenameModel {
    private String bucket_name;
    private String new_file_name;
    private String old_file_name;
    private ItemType itemType;
}