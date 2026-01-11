package com.home.cloud.service;

import com.home.cloud.jwt.JwtFilter;
import com.home.cloud.jwt.JwtUtil;
import com.home.cloud.model.AccountModel;
import com.home.cloud.model.LoginResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final JwtFilter jwtFilter;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthService(JwtUtil jwtUtil, JwtFilter jwtFilter) {
        this.jwtUtil = jwtUtil;
        this.jwtFilter = jwtFilter;
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

        String token = jwtUtil.generateToken(account.getUsername(), account.getAccount_id());

        return new LoginResponse(account.getAccount_id(), account.getUsername(), token);
    }

}