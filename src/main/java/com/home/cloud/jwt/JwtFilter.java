package com.home.cloud.jwt;

import com.home.cloud.model.AccountId;
import com.home.cloud.service.BucketService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Autowired
    private final BucketService bucketService;

    public JwtFilter(JwtUtil jwtUtil, BucketService bucketService) {
        this.jwtUtil = jwtUtil;
        this.bucketService = bucketService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Integer id = jwtUtil.getAccountId(token);
                String account_name = jwtUtil.extractAccountName(token);
                AccountId accountId = new AccountId(account_name, id);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        accountId, null, List.of()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof AccountId) {
            try {
                bucketService.createBucket();
            } catch (Exception e) {
                logger.warn("Bucket ensure failed", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}