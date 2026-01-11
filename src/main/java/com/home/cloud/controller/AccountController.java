package com.home.cloud.controller;

import com.home.cloud.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("cloud/auth/user")
public class AccountController {
    @Autowired
    private AccountService accountService;

}