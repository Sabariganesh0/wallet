package com.mainproject.wallet.mapper;

import com.mainproject.wallet.dto.TransactionDTO;
import com.mainproject.wallet.model.Transaction;

public class TransactionMapper {
    // Convert Transaction to TransactionDTO
    public static TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null; // Handle null transaction
        }

        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUserId());
        dto.setAmount(transaction.getAmount());
        dto.setTimestamp(transaction.getTimestamp());
        dto.setType(transaction.getType());
        dto.setSenderId(transaction.getSenderId()); // Set sender ID
        dto.setSenderUsername(transaction.getSenderUsername()); // Set sender username
        dto.setReceiverId(transaction.getReceiverId()); // Set receiver ID
        dto.setReceiverUsername(transaction.getReceiverUsername()); // Set receiver username

        return dto;
    }

    // Convert TransactionDTO to Transaction
    public static Transaction toEntity(TransactionDTO dto) {
        if (dto == null) {
            return null; // Handle null DTO
        }

        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setUserId(dto.getUserId());
        transaction.setAmount(dto.getAmount());
        transaction.setTimestamp(dto.getTimestamp());
        transaction.setType(dto.getType());
        transaction.setSenderId(dto.getSenderId()); // Set sender ID
        transaction.setSenderUsername(dto.getSenderUsername()); // Set sender username
        transaction.setReceiverId(dto.getReceiverId()); // Set receiver ID
        transaction.setReceiverUsername(dto.getReceiverUsername()); // Set receiver username

        return transaction;
    }
}
