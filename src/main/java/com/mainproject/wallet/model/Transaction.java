package com.mainproject.wallet.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    private String id;
    private String userId;  // The ID of the user who made the transaction
    private double amount;
    private LocalDateTime timestamp;
    private String type; // Transaction type (e.g., "Recharge", "Sent", "Received")

    // Updated fields for sender and receiver information
    private String senderId;       // The ID of the user who sent the money
    private String senderUsername;  // The username of the user who sent the money
    private String receiverId;     // The ID of the user who received the money
    private String receiverUsername; // The username of the user who received the money
}
