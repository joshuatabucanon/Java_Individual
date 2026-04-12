package com.bpi.m10activity1;


import java.util.HashSet;
import java.util.Set;

public class User {
    private String username;
    private String password;
    private Set<String> roles = new HashSet<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Set<String> getRoles() { return roles; }

    public void addRole(String role) {
        this.roles.add(role);
    }
}
