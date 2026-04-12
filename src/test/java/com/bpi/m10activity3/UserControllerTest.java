package com.bpi.m10activity3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // ----------------------------------------------------
    // ✅ POSITIVE SCENARIO: Successful user creation
    // ----------------------------------------------------
    @Test
    void createUser_success_returns201_andResponseBody() throws Exception {
        // Arrange
        CreateUserRequestDTO request = new CreateUserRequestDTO();
        request.setUsername("john");
        request.setPassword("password123");

        User savedUser = new User("john", "password123");

        when(userService.createUser(anyString(), anyString()))
                .thenReturn(savedUser);

        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.password").value("password123"));
    }

    // ----------------------------------------------------
    // ❌ NEGATIVE SCENARIO: Validation failure
    // Tests @Valid on DTO + GlobalExceptionHandler
    // ----------------------------------------------------
    @Test
    void createUser_invalidRequestBody_returns400() throws Exception {
        // Arrange
        CreateUserRequestDTO request = new CreateUserRequestDTO();
        request.setUsername("");      // @NotBlank violation
        request.setPassword("123");   // @Size(min=6) violation

        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // ----------------------------------------------------
    // ❌ NEGATIVE SCENARIO: Duplicate username
    // Tests GlobalExceptionHandler for UsernameAlreadyExistException
    // ----------------------------------------------------
    @Test
    void createUser_duplicateUsername_returns409() throws Exception {
        // Arrange
        CreateUserRequestDTO request = new CreateUserRequestDTO();
        request.setUsername("john");
        request.setPassword("password123");

        when(userService.createUser(anyString(), anyString()))
                .thenThrow(new UsernameAlreadyExistException("Username already exists"));

        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Username already exists"));
    }
}