package com.home.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountModel {
    private Integer account_id;
    private String account_username;
    private String account_email;
    private String account_password;
    private LocalDateTime created_at;
    private LocalDateTime updates_at;
}