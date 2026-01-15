package com.home.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FolderResponse {
    private String name;
    private long size;
}