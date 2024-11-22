package com.mainproject.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;

@Valid
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "Username should not be null or empty")
    private String username;       // Username for login

    @NotBlank(message = "Password should not be null or empty")
    private String password;       // Password for login
}
