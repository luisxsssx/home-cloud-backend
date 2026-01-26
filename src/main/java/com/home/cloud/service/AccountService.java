package com.home.cloud.service;

import com.home.cloud.exception.account.AccountException;
import com.home.cloud.model.AccountId;
import com.home.cloud.model.AccountModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.util.List;

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

   public List<AccountModel> getAccountInfo() {
        try {
            AccountId id = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Integer account_id = id.getAccount_id();
            return jdbcTemplate.query(
                    "select * from f_get_account_info(?)",
                    new Object[]{account_id},
                    (rs, rowNum) -> {
                        AccountModel account = new AccountModel();
                        account.setAccount_id(rs.getInt("out_account_id"));
                        account.setUsername(rs.getString("out_username"));
                        account.setEmail(rs.getString("out_email"));
                        account.setPassword(rs.getString("out_password"));
                        account.setCreated_at(rs.getTimestamp("out_created_at"));
                        account.setCreated_at(rs.getTimestamp("out_updated_at"));
                        account.setCreated_at(rs.getTimestamp("out_updated_at"));
                        return account;
                    }
            );
        } catch (Exception e) {
            throw new AccountException("Error getting data", e);
        }
   }

}