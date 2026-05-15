package com.home.cloud.service;

import com.home.cloud.exception.account.AccountException;
import com.home.cloud.exception.account.InvalidPasswordException;
import com.home.cloud.exception.BucketCreationException;
import com.home.cloud.model.AccountId;
import com.home.cloud.model.AccountModel;
import com.home.cloud.model.ChangePasswordRequest;
import com.home.cloud.model.UpdateAccountRequest;
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

    @Autowired
    private BucketService bucketService;

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

        Integer accountId = getAccountIdByUsername(accountModel.getUsername());
        if (accountId == null) {
            throw new AccountException("Account created but could not retrieve account ID", null);
        }

        try {
            bucketService.createBucket(accountId);
        } catch (BucketCreationException e) {
            throw new AccountException("Account created but bucket creation failed: " + e.getMessage(), e.getCause());
        }
    }

    private Integer getAccountIdByUsername(String username) {
        List<Integer> results = jdbcTemplate.queryForList(
                "SELECT account_id FROM account WHERE username = ?",
                Integer.class,
                username
        );
        return results.isEmpty() ? null : results.get(0);
    }

   public void changePassword(ChangePasswordRequest request) {
        try {
            AccountId id = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Integer accountId = id.getAccount_id();

            String storedHash = jdbcTemplate.queryForObject(
                    "SELECT password FROM account WHERE account_id = ?",
                    String.class,
                    accountId
            );

            if (!passwordEncoder.matches(request.getOldPassword(), storedHash)) {
                throw new InvalidPasswordException("Current password is incorrect");
            }

            String newHash = passwordEncoder.encode(request.getNewPassword());
            jdbcTemplate.update(
                    "UPDATE account SET password = ?, updated_at = CURRENT_TIMESTAMP WHERE account_id = ?",
                    newHash, accountId
            );
        } catch (InvalidPasswordException e) {
            throw e;
        } catch (Exception e) {
            throw new AccountException("Error changing password", e);
        }
   }

   public AccountModel updateAccount(UpdateAccountRequest request) {
        try {
            AccountId id = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Integer account_id = id.getAccount_id();
            return jdbcTemplate.queryForObject(
                    "select * from f_update_account(?, ?, ?)",
                    new Object[]{account_id, request.getUsername(), request.getEmail()},
                    (rs, rowNum) -> {
                        AccountModel account = new AccountModel();
                        account.setAccount_id(rs.getInt("out_account_id"));
                        account.setUsername(rs.getString("out_username"));
                        account.setEmail(rs.getString("out_email"));
                        account.setUpdated_at(rs.getTimestamp("out_updated_at"));
                        return account;
                    }
            );
        } catch (Exception e) {
            throw new AccountException("Error updating account", e);
        }
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
                        account.setUpdated_at(rs.getTimestamp("out_updated_at"));
                        return account;
                    }
            );
        } catch (Exception e) {
            throw new AccountException("Error getting data", e);
        }
   }

}