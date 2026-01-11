package com.home.cloud.service;

import com.home.cloud.model.AccountModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;

@Service
public class AccountService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createAccount(AccountModel accountModel) {
        String encodePassword = passwordEncoder.encode(accountModel.getPassword());
        jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
            CallableStatement cs =
                    connection.prepareCall("call sp_create_account(?,?,?)");

            cs.setString(1, accountModel.getUsername());
            cs.setString(2, accountModel.getEmail());
            cs.setString(3, encodePassword);

            cs.execute();
            return null;
        });
    }

}