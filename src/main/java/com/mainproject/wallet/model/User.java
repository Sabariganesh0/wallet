package com.mainproject.wallet.model;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import org.springframework.stereotype.Component;

@Document
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
    @NotNull
    private String username;
    @NotNull
    private String password; // Store hashed password
    @NotNull
    private String email;
    @Min(value = 0, message = "Minimum balance to be maintained")
    private double walletBalance;

    // Optimistic Locking field
    @Version
    private Long version;
}
