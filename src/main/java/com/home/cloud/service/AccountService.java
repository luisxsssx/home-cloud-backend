package com.home.cloud.service;

import com.home.cloud.model.AccountModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;

@Service
public class AccountService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createAccount(AccountModel accountModel) {
        jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
            CallableStatement cs =
                    connection.prepareCall("call sp_create_account(?,?,?)");

            cs.setString(1, accountModel.getAccount_username());
            cs.setString(2, accountModel.getAccount_email());
            cs.setString(3, accountModel.getAccount_password());

            cs.execute();
            return null;
        });
    }


}