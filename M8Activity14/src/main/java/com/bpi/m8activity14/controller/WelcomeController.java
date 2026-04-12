package com.bpi.m8activity14.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/welcome")
public class WelcomeController {
    @Value("${welcome.message}")
    private String message;

    @GetMapping
    public String showWelcomeMessage() {
        return message;
    }
}
