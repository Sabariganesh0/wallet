package com.mainproject.wallet.controller;

import com.mainproject.wallet.dto.ErrorResponseDTO;
import com.mainproject.wallet.dto.LoginDTO;
import com.mainproject.wallet.dto.RegisterDTO;
import com.mainproject.wallet.dto.UserDTO;
import com.mainproject.wallet.mapper.UserMapper;
import com.mainproject.wallet.model.User;
import com.mainproject.wallet.service.UserService;
import com.mainproject.wallet.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    // Reused DTOs and variables
    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;
    private User user;
    private UserDTO userDTO;
    private String token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize reused variables
        registerDTO = new RegisterDTO("username", "email@example.com", "password");
        user = new User("1", "username", "hashedPassword", "email@example.com", 0.0, 0L);
        userDTO = new UserDTO("1", "username", "email@example.com", 0.0, null);
        token = "generatedToken";
        loginDTO = new LoginDTO("username", "password");
    }

    @Test
    void testRegister_ShouldReturnUserDTO_WhenRegistrationIsSuccessful() {
        // Arrange
        when(userService.register(registerDTO)).thenReturn(user);

        // Act
        ResponseEntity<UserDTO> response = authController.register(registerDTO);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDTO, response.getBody());
        verify(userService, times(1)).register(registerDTO);
    }

    @Test
    void testLogin_ShouldReturnUserDTO_WithToken_WhenLoginIsSuccessful() {
        // Arrange
        when(userService.login(loginDTO)).thenReturn(user);
        when(jwtUtil.generateToken(user.getUsername())).thenReturn(token);

        // Act
        ResponseEntity<?> response = authController.login(loginDTO);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        userDTO.setToken(token); // Set the token for comparison
        assertEquals(userDTO, response.getBody());
        verify(userService, times(1)).login(loginDTO);
        verify(jwtUtil, times(1)).generateToken(user.getUsername());
    }

    @Test
    void testLogin_ShouldReturnBadRequest_WhenUserNotFound() {
        // Arrange
        LoginDTO invalidLoginDTO = new LoginDTO("username", "wrongPassword");
        when(userService.login(invalidLoginDTO)).thenReturn(null);

        // Act
        ResponseEntity<?> response = authController.login(invalidLoginDTO);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        verify(userService, times(1)).login(invalidLoginDTO);
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
