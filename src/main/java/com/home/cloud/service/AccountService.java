package com.home.cloud.service;

import com.home.cloud.jwt.JwtUtil;
import com.home.cloud.model.AccountModel;
import com.home.cloud.model.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;

@Service
public class AccountService {

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AccountService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

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

    public LoginResponse login(String username, String password) throws Exception {
        String query = "select * from f_get_account(?)";
        AccountModel account = jdbcTemplate.queryForObject(
                query,
                new Object[]{username},
                (rs, rowNum) -> {
                    AccountModel a = new AccountModel();
                    a.setAccount_id(rs.getInt("out_account_id"));
                    a.setUsername(rs.getString("out_username"));
                    a.setEmail(rs.getString("out_email"));
                    a.setPassword(rs.getString("out_password"));
                    a.setCreated_at(rs.getTimestamp("out_created_at"));
                    a.setUpdated_at(rs.getTimestamp("out_updated_at"));
                    return a;
                }
        );

        if (account == null) {
            throw new Exception("User not found");
        }

        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new Exception("Invalid password");
        }

        String token = jwtUtil.generateToken(account.getUsername());

        return new LoginResponse(token, account.getUsername());
    }
}