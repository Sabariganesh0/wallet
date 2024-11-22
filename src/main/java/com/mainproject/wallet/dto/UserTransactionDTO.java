package com.mainproject.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class UserTransactionDTO {
    @NotBlank(message = "User Id cannot be null or Empty")
    private String userId;
    private List<TransactionDTO> transactions;
}
