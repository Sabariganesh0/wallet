package com.mainproject.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    @NotBlank(message = "Transaction ID cannot be null or empty")
    private String id;

    @NotBlank(message = "User ID cannot be null or empty")
    private String userId;

    @NotNull(message = "Amount cannot be null")
    private double amount;

    private LocalDateTime timestamp;

    @NotBlank(message = "Transaction type cannot be null or empty") // Transaction type
    private String type;

    private String senderId;       // The ID of the user who sent the money
    private String senderUsername;  // The username of the user who sent the money
    private String receiverId;     // The ID of the user who received the money
    private String receiverUsername; // The username of the user who received the money
}
