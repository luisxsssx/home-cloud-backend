package com.home.cloud.model;

import com.home.cloud.model.type.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeleteItemModel {
    private String name;
    private ItemType itemType;
}