package com.mainproject.wallet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotBlank(message = "User ID cannot be null or empty")
    private String id;

    @NotBlank(message = "Username cannot be null or empty")
    private String username;

    @NotBlank(message = "Email cannot be null or empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Wallet balance cannot be null")
    @Min(value = 0, message = "Wallet cannot have a balance less than 0")
    private double walletBalance;

    // Added token field for JWT
    private String token; // This will hold the JWT token if you want to return it upon login
}
