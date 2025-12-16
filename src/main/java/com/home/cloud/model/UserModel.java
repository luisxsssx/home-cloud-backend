package com.home.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserModel {
    private Integer user_id;
    private String username;
    private String email;
    private LocalDateTime created_at;
    private LocalDateTime updates_at;
}