package com.home.cloud.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Generate token
    public String generateToken(String account_name, Integer account_id) {
        return Jwts.builder()
                .subject(account_name)
                .claim("username", account_name)
                .claim("account_id", account_id)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractAccountName(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Integer getAccountId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret.getBytes())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("account_id", Integer.class);
    }

}