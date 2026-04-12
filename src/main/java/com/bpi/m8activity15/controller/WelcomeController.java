package com.bpi.m8activity15.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/welcome")
public class WelcomeController {

    private final String message;

    public WelcomeController(@Value("${welcome.message}") String message) {
        this.message = message;
    }
    
    @GetMapping
    public String showWelcomeMessage() {
        return message;
    }
}
