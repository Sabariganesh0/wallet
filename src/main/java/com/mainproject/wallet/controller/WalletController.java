package com.mainproject.wallet.controller;

import com.mainproject.wallet.dto.ErrorResponseDTO;
import com.mainproject.wallet.dto.RechargeResponseDTO;
import com.mainproject.wallet.dto.TransactionDTO;
import com.mainproject.wallet.dto.UserDTO;
import com.mainproject.wallet.mapper.UserMapper;
import com.mainproject.wallet.service.TransactionService;
import com.mainproject.wallet.service.WalletService;
import com.mainproject.wallet.utils.JwtUtil;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final TransactionService transactionService;
    private final JwtUtil jwtUtil;

    // Constructor injection
    public WalletController(WalletService walletService, TransactionService transactionService, JwtUtil jwtUtil) {
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.jwtUtil = jwtUtil;
    }

    // Helper method to validate JWT and extract the username
    private String validateTokenAndExtractUsername(String token) {
        String username = token.substring(7); // Remove 'Bearer ' prefix
        if (!jwtUtil.isTokenValid(username, username)) {
            return null;
        }
        return jwtUtil.extractUsername(username);
    }

    @PostMapping("/recharge")
    public ResponseEntity<?> recharge(
            @RequestHeader("Authorization") String token, // Get token from header
            @Min(value = 1, message = "Amount must be greater than zero") @RequestParam double amount) {

        String username = validateTokenAndExtractUsername(token); // Validate token and extract username
        if (username == null) {
            return ResponseEntity.status(401).body(new ErrorResponseDTO("Unauthorized access"));
        }

        return walletService.recharge(username, amount); // Directly return the service response
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @RequestHeader("Authorization") String token, // Get token from header
            @RequestParam String toUsername, @Min(value = 1, message = "Amount must be greater than zero") @RequestParam double amount) {

        String fromUsername = validateTokenAndExtractUsername(token); // Validate token and extract sender's username
        if (fromUsername == null) {
            return ResponseEntity.status(401).body(new ErrorResponseDTO("Unauthorized access"));
        }

        // Check if the user has sufficient balance and other business logic for transfer
        UserDTO userDTO = UserMapper.toDTO(walletService.transfer(fromUsername, toUsername, amount), token);
        return userDTO != null ? ResponseEntity.ok(userDTO) : ResponseEntity.badRequest().body(new ErrorResponseDTO("Transfer failed"));
    }

    @GetMapping("/statement")
    public ResponseEntity<?> viewStatement(
            @RequestHeader("Authorization") String token) { // Get token from header

        String username = validateTokenAndExtractUsername(token); // Validate token and extract username
        if (username == null) {
            return ResponseEntity.status(401).body(new ErrorResponseDTO("Unauthorized access"));
        }

        UserDTO userDTO = UserMapper.toDTO(walletService.viewStatement(username), token);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/transactions/{username}")
    public ResponseEntity<?> getTransactions(
            @RequestHeader("Authorization") String token, // Get token from header
            @PathVariable String username) {

        String requestUsername = validateTokenAndExtractUsername(token); // Validate token and extract the requesting username
        if (requestUsername == null) {
            return ResponseEntity.status(401).body(new ErrorResponseDTO("Unauthorized access"));
        }

        // Ensure the requestor is allowed to view the transactions of the provided username
        if (!requestUsername.equals(username)) {
            return ResponseEntity.status(403).body(new ErrorResponseDTO("Forbidden: You are not authorized to view these transactions"));
        }

        List<TransactionDTO> transactions = transactionService.getTransactionsByUsername(username);
        return ResponseEntity.ok(transactions); // This will return 200 OK with an empty array if no transactions found
    }

    @GetMapping("/cashbacks/{username}")
    public ResponseEntity<?> getAllCashbacks(
            @RequestHeader("Authorization") String token, // Get token from header
            @PathVariable String username) {

        String requestUsername = validateTokenAndExtractUsername(token); // Validate token and extract the requesting username
        if (requestUsername == null) {
            return ResponseEntity.status(401).body(new ErrorResponseDTO("Unauthorized access"));
        }

        // Ensure the requestor is allowed to view the cashback transactions of the provided username
        if (!requestUsername.equals(username)) {
            return ResponseEntity.status(403).body(new ErrorResponseDTO("Forbidden: You are not authorized to view cashback data"));
        }

        List<TransactionDTO> cashbacks = transactionService.getCashbackByUsername(username);
        return ResponseEntity.ok(cashbacks);
    }
}
