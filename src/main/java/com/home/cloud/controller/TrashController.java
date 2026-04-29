package com.home.cloud.controller;

import com.home.cloud.model.TrashModel;
import com.home.cloud.service.TrashService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("cloud/move")
public class TrashController {
    private final TrashService trashService;

    public TrashController(TrashService trashService) {
        this.trashService = trashService;
    }

    @PostMapping("/data")
    public ResponseEntity<?> moveObject(@RequestBody TrashModel trashModel) throws Exception {
        trashService.moveObject(trashModel.getSourcePath(), trashModel.getTargetPath());
        return ResponseEntity.ok(Map.of("message", "Object moved successfully", "item", trashModel));
    }
}