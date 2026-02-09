package com.home.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountModel {
    private Integer account_id;
    private String username;
    private String email;
    private String password;
    private Timestamp created_at;
    private Timestamp updated_at;
    private Integer bucket_id;
}