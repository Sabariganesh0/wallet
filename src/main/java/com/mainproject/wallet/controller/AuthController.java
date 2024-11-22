package com.mainproject.wallet.controller;

import com.mainproject.wallet.dto.ErrorResponseDTO;
import com.mainproject.wallet.dto.LoginDTO;
import com.mainproject.wallet.dto.RegisterDTO;
import com.mainproject.wallet.dto.UserDTO;
import com.mainproject.wallet.mapper.UserMapper;
import com.mainproject.wallet.model.User;
import com.mainproject.wallet.service.UserService;
import com.mainproject.wallet.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        User user = userService.register(registerDTO);
        UserDTO userDTO = UserMapper.toDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO);
        if (user != null) {
            String token = jwtUtil.generateToken(user.getUsername());
            UserDTO userDTO = UserMapper.toDTO(user, token);
            return ResponseEntity.ok(userDTO);
        }
        else {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Invalid credentials.")); // ErrorResponseDTO class to handle error messages
        }
    }
}
