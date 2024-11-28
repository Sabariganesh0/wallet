package com.mainproject.wallet.service.impl;

import com.mainproject.wallet.dto.LoginDTO;
import com.mainproject.wallet.dto.RegisterDTO;
import com.mainproject.wallet.exception.AuthException;
import com.mainproject.wallet.model.User;
import com.mainproject.wallet.repository.UserRepository;
import com.mainproject.wallet.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // Constants for tests
    private static final String USERNAME = "testUser";
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password";
    private static final String USER_ID = "1";

    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize common objects
        registerDTO = new RegisterDTO();
        registerDTO.setUsername(USERNAME);
        registerDTO.setEmail(EMAIL);
        registerDTO.setPassword(PASSWORD);

        loginDTO = new LoginDTO();
        loginDTO.setUsername(USERNAME);
        loginDTO.setPassword(PASSWORD);

        user = new User();
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        user.setPassword("encryptedPassword");
        user.setId(USER_ID);
    }

    @Test
    void testRegister_NullUsername_throwsIllegalArgumentException() {
        registerDTO.setUsername(null);
        assertThrows(NullPointerException.class, () -> userService.register(registerDTO));
    }

    @Test
    void testRegister_NullEmail_throwsNullPointerException() {
        registerDTO.setEmail(null);
        assertThrows(NullPointerException.class, () -> userService.register(registerDTO));
    }

    @Test
    void testRegister_NullPassword_throwsIllegalArgumentException() {
        registerDTO.setPassword(null);
        assertThrows(NullPointerException.class, () -> userService.register(registerDTO));
    }

    @Test
    void testRegister_UsernameAndEmailExists_throwsAuthException() {
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(new User());
        when(userRepository.findByEmail(EMAIL)).thenReturn(new User());

        AuthException exception = assertThrows(AuthException.class, () -> userService.register(registerDTO));
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void testLogin_InvalidPassword_throwsAuthException() {
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false);

        loginDTO.setPassword("wrongPassword");
        AuthException exception = assertThrows(AuthException.class, () -> userService.login(loginDTO));
        assertEquals("Invalid Credentials", exception.getMessage());
    }

    @Test
    void testGetUserIdByUsername_NullUsername() {
        assertNull(userService.getUserIdByUsername(null));
    }

    @Test
    void testGetEmailByUsername_NullUsername() {
        assertNull(userService.getEmailByUsername(null));
    }

    @Test
    void testLogin_NullValues_throwsAuthException() {
        loginDTO.setUsername(null);
        loginDTO.setPassword(null);

        AuthException exception = assertThrows(AuthException.class, () -> userService.login(loginDTO));
        assertEquals("Invalid Credentials", exception.getMessage());
    }

    @Test
    void testRegister_PasswordEncoding_verifiesEncodedPassword() {
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(null);
        when(userRepository.findByEmail(EMAIL)).thenReturn(null);
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encryptedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.register(registerDTO);
        verify(passwordEncoder).encode(PASSWORD);
    }

    @Test
    void testRegister_Success_returnsUser() {
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(null);
        when(userRepository.findByEmail(EMAIL)).thenReturn(null);
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encryptedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userService.register(registerDTO);
        assertNotNull(registeredUser);
        assertEquals(USERNAME, registeredUser.getUsername());
        assertEquals(EMAIL, registeredUser.getEmail());
        assertEquals("encryptedPassword", registeredUser.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testLogin_Success_returnsUser() {
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(user);
        when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(true);

        User loggedInUser = userService.login(loginDTO);
        assertNotNull(loggedInUser);
        assertEquals(USERNAME, loggedInUser.getUsername());
    }

    @Test
    void testRegister_EmailExists_throwsAuthException() {
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(null);
        when(userRepository.findByEmail(EMAIL)).thenReturn(new User());

        AuthException exception = assertThrows(AuthException.class, () -> userService.register(registerDTO));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void testGetUserIdByUsername_UserExists_returnsUserId() {
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(user);
        String userId = userService.getUserIdByUsername(USERNAME);
        assertEquals(USER_ID, userId);
    }

    @Test
    void testGetEmailByUsername_UserExists_returnsEmail() {
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(user);
        String email = userService.getEmailByUsername(USERNAME);
        assertEquals(EMAIL, email);
    }
}
