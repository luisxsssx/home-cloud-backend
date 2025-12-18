package com.home.cloud.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountFolder {
    private Integer folder_id;
    private String folder_name;
    private Integer account_id;
    private Integer bucket_id;
}