package com.bpi.m10activity2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for UserService using H2 in-memory database.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    // -------------------------
    // Create User
    // -------------------------

    @Test
    void test1_createUser_success() {
        // Arrange
        String username = "john";
        String password = "pass123";

        // Act
        User user = userService.createUser(username, password);

        // Assert
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(1, userRepository.count());
    }

    @Test
    void test2_createUser_duplicateUsername_fails() {
        // Arrange
        userService.createUser("john", "pass123");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser("john", "anotherPass")
        );
    }

    // -------------------------
    // Duplicate check
    // -------------------------

    @Test
    void test3_isDuplicateUsername_true() {
        // Arrange
        userService.createUser("john", "pass");

        // Act
        boolean result = userService.isDuplicateUsername("john");

        // Assert
        assertTrue(result);
    }

    @Test
    void test4_isDuplicateUsername_false() {
        // Arrange

        // Act
        boolean result = userService.isDuplicateUsername("jane");

        // Assert
        assertFalse(result);
    }

    // -------------------------
    // Assign roles
    // -------------------------

    @Test
    void test5_assignRole_success() {
        // Arrange
        userService.createUser("john", "pass");

        // Act
        User updated = userService.assignRole("john", "ADMIN");

        // Assert
        assertTrue(updated.getRoles().contains("ADMIN"));
    }

    @Test
    void test6_assignRole_userNotFound_fails() {
        // Arrange

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () ->
                userService.assignRole("ghost", "ADMIN")
        );
    }
}