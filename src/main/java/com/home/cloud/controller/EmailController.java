package com.home.cloud.controller;

import com.home.cloud.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/send")
    public String sendEmail() {
        emailService.sendSimpleEmail(
                "lc302821@gmail.com",
                "Home Cloud",
                "Hello from Spring Boot!",
                """
                               Hi %s,
                               
                               Welcome to Home Cloud! ☁️
                               
                               Thank you for signing up to use our services. We're excited to help you \
                               manage your data safely and easily.
                               
                               To get started, we recommend checking out your dashboard:
                               👉 http://localhost:4200/login
                               
                               If you have any questions, simply reply to this email. Our team is here to help!
                               
                               Best regards,
                               The Home Cloud Team"""

        );
        return "Email sent!";
    }
}