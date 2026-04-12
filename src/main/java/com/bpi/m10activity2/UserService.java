package com.bpi.m10activity2;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User newUser = new User(username, password);
        return userRepository.save(newUser);
    }

    public boolean isDuplicateUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User assignRole(String username, String role) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.addRole(role);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}