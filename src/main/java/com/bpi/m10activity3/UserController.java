package com.bpi.m10activity3;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(
            @Valid @RequestBody CreateUserRequestDTO request) {

        User user = userService.createUser(
                request.getUsername(),
                request.getPassword()
        );

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}