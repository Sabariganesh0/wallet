package com.mainproject.wallet.service;

import com.mainproject.wallet.dto.LoginDTO;
import com.mainproject.wallet.dto.RegisterDTO;
import com.mainproject.wallet.exception.AuthException;
import com.mainproject.wallet.mapper.UserMapper;
import com.mainproject.wallet.model.User;
import com.mainproject.wallet.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterDTO registerDTO) {

        // Check for existing user
        if (userRepository.findByUsernameIgnoreCase(registerDTO.getUsername()) != null) {
            throw new AuthException("Username already exists");
        }
        if (userRepository.findByEmail(registerDTO.getEmail()) != null) {
            throw new AuthException("Email already exists");
        }

        // Encrypt password and save user
        String encryptedPassword = passwordEncoder.encode(registerDTO.getPassword());
        User newUser = UserMapper.toEntity(registerDTO);
        newUser.setPassword(encryptedPassword);
        newUser.setWalletBalance(0.0);

        User savedUser = userRepository.save(newUser);
        log.info("User registered with ID: " + savedUser.getId());

        return savedUser;
    }

    public User login(LoginDTO loginDTO) {
        User user = userRepository.findByUsernameIgnoreCase(loginDTO.getUsername());
        if (user != null && passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return user;
        }
        throw new AuthException("Invalid Credentials");
    }

    public String getUserIdByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username);
        return user != null ? user.getId() : null;
    }

    public String getEmailByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username);
        return user != null ? user.getEmail() : null;
    }
}
