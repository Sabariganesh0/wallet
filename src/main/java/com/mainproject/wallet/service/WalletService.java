package com.mainproject.wallet.service;

import com.mainproject.wallet.constant.TransactionENUM;
import com.mainproject.wallet.dto.RechargeResponseDTO;
import com.mainproject.wallet.dto.TransactionDTO;
import com.mainproject.wallet.exception.UserNotFoundException;
import com.mainproject.wallet.exception.WalletException;
import com.mainproject.wallet.model.User;
import com.mainproject.wallet.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class WalletService {
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final UserService userService;

    @Value("${cashback.lower.percent}")
    double cashbackLowerPercent;

    @Value("${cashback.upper.percent}")
    double cashbackUpperPercent;


    public WalletService(UserRepository userRepository, TransactionService transactionService, UserService userService) {
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.userService = userService;
    }


    @Transactional
    public ResponseEntity<RechargeResponseDTO> recharge(String username, double amount) {
        String userId = userService.getUserIdByUsername(username);
        if (userId == null) {
            throw new WalletException("No user found for username: " + username);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new WalletException("No user found for userId: " + userId));
        log.info("Current balance for userId {}: {}", userId, user.getWalletBalance());

        // Recharge the user's wallet balance
        user.setWalletBalance(user.getWalletBalance() + amount);
        try {
            userRepository.save(user); // Update the balance in the user collection
        } catch (OptimisticLockingFailureException e) {
            log.error("Optimistic locking failure when saving user with ID {}: {}", userId, e.getMessage());
            throw new WalletException("Concurrency conflict occurred while updating the balance. Please try again.");
        }

        // Calculate cashback within limits
        double cashbackAmount = amount * (Math.random() * (cashbackUpperPercent - cashbackLowerPercent) + cashbackLowerPercent);
        int roundedCashback = (int) Math.round(cashbackAmount / 100);

        // Apply cashback if it's greater than 0
        if (roundedCashback > 0) {
            user.setWalletBalance(user.getWalletBalance() + roundedCashback);
            try {
                userRepository.save(user); // Update the balance in the user collection
            } catch (OptimisticLockingFailureException e) {
                log.error("Optimistic locking failure when saving user with ID {}: {}", userId, e.getMessage());
                throw new WalletException("Concurrency conflict occurred while updating the balance. Please try again.");
            }

            // Record cashback transaction
            transactionService.recordTransaction(username, roundedCashback, TransactionENUM.CASHBACK, userId, username,cashbackAmount);
            log.info("Cashback of {} applied for userId {}", roundedCashback, userId);
        } else {
            log.info("No cashback applied for userId {}", userId);
        }

        // Record recharge transaction
        TransactionDTO transactionDTO = transactionService.recordTransaction(username, amount, TransactionENUM.RECHARGE, userId, username, roundedCashback);
        log.info("Transaction recorded: {}", transactionDTO);

        // Create the response DTO
        RechargeResponseDTO responseDTO = new RechargeResponseDTO(roundedCashback, user.getWalletBalance());
        return ResponseEntity.ok(responseDTO);
    }

    @Transactional
    public User transfer(String fromUsername, String toUsername, double amount) {
        // Check if the sender and receiver are the same

        log.info("fromUsername"+fromUsername);
        log.info("tousername"+toUsername);
        if (fromUsername.equals(toUsername)) {
            throw new WalletException("Cannot transfer money to yourself");
        }

        String fromUserId = userService.getUserIdByUsername(fromUsername);
        String toUserId = userService.getUserIdByUsername(toUsername);

        if (fromUserId == null || toUserId == null) {
            throw new UserNotFoundException("Wallet not found for username " + toUsername);
        }

        User fromUser = userRepository.findById(fromUserId).orElseThrow(() -> new WalletException("User not found for userId: " + fromUserId));
        User toUser = userRepository.findById(toUserId).orElseThrow(() -> new WalletException("User not found for userId: " + toUserId));

        if (fromUser.getWalletBalance() >= amount) {
            fromUser.setWalletBalance(fromUser.getWalletBalance() - amount);
            toUser.setWalletBalance(toUser.getWalletBalance() + amount);
            try {
                userRepository.save(fromUser); // Update the balance in the user collection
            } catch (OptimisticLockingFailureException e) {
                log.error("Optimistic locking failure when saving user with ID {}: {}", fromUserId, e.getMessage());
                throw new WalletException("Concurrency conflict occurred while updating the balance. Please try again.");
            }
            try {
                userRepository.save(toUser); // Update the balance in the user collection
            } catch (OptimisticLockingFailureException e) {
                log.error("Optimistic locking failure when saving user with ID {}: {}", toUserId, e.getMessage());
                throw new WalletException("Concurrency conflict occurred while updating the balance. Please try again.");
            }

            // Record transactions
            transactionService.recordTransaction(fromUsername, amount, TransactionENUM.SENT, toUserId, toUsername, 0);
            TransactionDTO transactionDTO = transactionService.recordTransaction(fromUsername, amount, TransactionENUM.RECEIVED, toUserId, toUsername, 0);
            log.info("Transaction recorded for transfer: {}", transactionDTO);
            return toUser;
        }

        throw new WalletException("Insufficient funds in the account");
    }

    public User viewStatement(String username) {
        String userId = userService.getUserIdByUsername(username);
        if (userId == null) {
            throw new UserNotFoundException("No user found for username: " + username);
        }

        log.info("Retrieving statement for username: {}", username);
        return userRepository.findById(userId).orElseThrow(() -> new WalletException("No user found for userId: " + userId));
    }
}
