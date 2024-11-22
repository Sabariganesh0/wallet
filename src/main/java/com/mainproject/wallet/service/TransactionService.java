package com.mainproject.wallet.service;

import com.mainproject.wallet.constant.TransactionENUM;
import com.mainproject.wallet.dto.TransactionDTO;
import com.mainproject.wallet.exception.WalletException;
import com.mainproject.wallet.mapper.TransactionMapper;
import com.mainproject.wallet.model.Transaction;
import com.mainproject.wallet.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final EmailService emailService;

    public TransactionService(TransactionRepository transactionRepository, UserService userService, EmailService emailService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    public TransactionDTO recordTransaction(String username, double amount, String type, String receiverId, String receiverUsername, double cashbackAmount) {
        String userId = userService.getUserIdByUsername(username);
        if (userId == null) {
            throw new WalletException("User not found for username: " + username);
        }

        // Create and set up the transaction instance
        Transaction transaction = createTransaction(username, userId, amount, type, receiverId, receiverUsername);

        // Save the transaction
        transactionRepository.save(transaction);

        // Fetch email addresses
        String userEmail = userService.getEmailByUsername(username);
        String receiverEmail = userService.getEmailByUsername(receiverUsername);

        // Send email notifications
        if (type.equals(TransactionENUM.RECHARGE)) {
            emailService.sendRechargeEmail(userEmail, amount, cashbackAmount);
        } else if (type.equals(TransactionENUM.SENT)) {
            emailService.sendTransferEmail(receiverEmail, username, userEmail, receiverUsername, amount);
        }

        return TransactionMapper.toDTO(transaction);
    }

    private Transaction createTransaction(String username, String userId, double amount, String type, String receiverId, String receiverUsername) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);

        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setType(type);

        // Set sender ID to null for recharge and cashback transactions
        if (TransactionENUM.RECHARGE.equals(type) || TransactionENUM.CASHBACK.equals(type)) {
            transaction.setSenderId(null);
            transaction.setSenderUsername(null);
        } else {
            transaction.setSenderId(userId);
            transaction.setSenderUsername(username);
        }

        if(TransactionENUM.RECEIVED.equals(type))
        {
            transaction.setUserId(receiverId);
        }

        // Set receiver details
        transaction.setReceiverId(receiverId);
        transaction.setReceiverUsername(receiverUsername);

        return transaction;
    }

    public List<TransactionDTO> getTransactionsByUserId(String userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream()
                .map(TransactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByUsername(String username) {
        String userId = userService.getUserIdByUsername(username);
        if (userId == null) {
            throw new WalletException("User not found for username: " + username);
        }
        return getTransactionsByUserId(userId);
    }

    public TransactionDTO getTransactionById(String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        if (transaction.isEmpty()) {
            throw new WalletException("Transaction not found for ID: " + transactionId);
        }
        return TransactionMapper.toDTO(transaction.orElse(null));
    }

    public List<TransactionDTO> getCashbackByUserId(String userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream()
                .filter(transaction -> TransactionENUM.CASHBACK.equals(transaction.getType()))
                .map(TransactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getCashbackByUsername(String username) {
        String userId = userService.getUserIdByUsername(username);
        if (userId == null) {
            throw new WalletException("User not found for username: " + username);
        }
        return getCashbackByUserId(userId);
    }
}
