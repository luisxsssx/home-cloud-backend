package com.home.cloud.controller;

import com.home.cloud.model.AccountModel;
import com.home.cloud.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("cloud/auth/user")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/list")
    public List<AccountModel> accountInfo() {
        return accountService.getAccountInfo();
    }

}