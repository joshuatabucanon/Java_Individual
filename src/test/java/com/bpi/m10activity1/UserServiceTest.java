package com.bpi.m10activity1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    // -------------------------
    // Create User
    // -------------------------
    @Test
    void test1_createUser_success() {
        String username = "john";
        String password = "pass123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.createUser(username, password);

        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void test2_createUser_duplicateUsername_fails() {
        String username = "john";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(new User(username, "pass")));

        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(username, "pass2")
        );
    }

    // -------------------------
    // Duplicate check
    // -------------------------
    @Test
    void test3_isDuplicateUsername_true() {
        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(new User("john", "pass")));

        assertTrue(userService.isDuplicateUsername("john"));
    }

    @Test
    void test4_isDuplicateUsername_false() {
        when(userRepository.findByUsername("jane"))
                .thenReturn(Optional.empty());

        assertFalse(userService.isDuplicateUsername("jane"));
    }

    // -------------------------
    // Assign roles
    // -------------------------
    @Test
    void test5_assignRole_success() {
        User user = new User("john", "pass");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.assignRole("john", "ADMIN");

        assertTrue(updated.getRoles().contains("ADMIN"));
    }

    @Test
    void test6_assignRole_userNotFound_fails() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                userService.assignRole("ghost", "ADMIN")
        );
    }
}
