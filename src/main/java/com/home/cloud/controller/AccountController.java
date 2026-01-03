package com.home.cloud.controller;

import com.home.cloud.model.AccountModel;
import com.home.cloud.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("cloud/auth/user")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody AccountModel accountModel) {
        try {
            accountService.createAccount(accountModel);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Account registered successfully");
            System.out.println("Body" + response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed register");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}