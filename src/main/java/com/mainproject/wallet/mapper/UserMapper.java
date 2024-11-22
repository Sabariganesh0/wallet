package com.mainproject.wallet.mapper;

import com.mainproject.wallet.dto.RegisterDTO;
import com.mainproject.wallet.dto.UserDTO;
import com.mainproject.wallet.model.User;

public class UserMapper {

    public static UserDTO toDTO(User user, String token) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setWalletBalance(user.getWalletBalance());
        dto.setToken(token);
        return dto;
    }

    public static UserDTO toDTO(User user) {
        return toDTO(user, null);
    }

    public static User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setWalletBalance(dto.getWalletBalance());
        return user;
    }

    // New method to convert RegisterDTO to User
    public static User toEntity(RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        user.setWalletBalance(0); // Set initial wallet balance as 0
        return user;
    }
}
