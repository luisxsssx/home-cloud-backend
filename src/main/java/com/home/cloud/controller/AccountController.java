package com.home.cloud.controller;

import com.home.cloud.model.AccountModel;
import com.home.cloud.model.ChangePasswordRequest;
import com.home.cloud.model.UpdateAccountRequest;
import com.home.cloud.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("cloud/auth/user")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/list")
    public List<AccountModel> accountInfo() {
        return accountService.getAccountInfo();
    }

    @PostMapping("/update")
    public AccountModel updateAccount(@Valid @RequestBody UpdateAccountRequest request) {
        return accountService.updateAccount(request);
    }

    @PostMapping("/change-password")
    public Map<String, String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        accountService.changePassword(request);
        return Map.of("message", "Password changed successfully");
    }

}