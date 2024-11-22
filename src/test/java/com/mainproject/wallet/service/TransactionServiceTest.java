package com.mainproject.wallet.service;

import com.mainproject.wallet.constant.TransactionENUM;
import com.mainproject.wallet.dto.TransactionDTO;
import com.mainproject.wallet.exception.WalletException;
import com.mainproject.wallet.model.Transaction;
import com.mainproject.wallet.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceTest.class);

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TransactionService transactionService;

    // Reused variables
    private final String testUsername = "testUser";
    private final String receiverUsername = "receiverUser";
    private final String userId = "userId";
    private final String receiverId = "receiverId";
    private final double rechargeAmount = 100.0;
    private final double transferAmount = 50.0;
    private final String transactionId = "transactionId";

    private Transaction transaction;
    private Transaction transactionSent;
    private Transaction transactionCashback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize transactions
        transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(rechargeAmount);
        transaction.setType(TransactionENUM.RECHARGE);

        transactionSent = new Transaction();
        transactionSent.setUserId(userId);
        transactionSent.setAmount(transferAmount);
        transactionSent.setType(TransactionENUM.SENT);

        transactionCashback = new Transaction();
        transactionCashback.setUserId(userId);
        transactionCashback.setAmount(transferAmount);
        transactionCashback.setType(TransactionENUM.CASHBACK);
    }

    @Test
    void testRecordTransactionRecharge() {
        // Arrange
        when(userService.getUserIdByUsername(testUsername)).thenReturn(userId);
        when(userService.getEmailByUsername(testUsername)).thenReturn("test@example.com");
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // Act
        TransactionDTO transactionDTO = transactionService.recordTransaction(testUsername, rechargeAmount, TransactionENUM.RECHARGE, null, null, 0);

        // Assert
        assertNotNull(transactionDTO);
        assertEquals(transaction.getAmount(), transactionDTO.getAmount());
        verify(emailService).sendRechargeEmail("test@example.com", rechargeAmount, 0.0);
    }

    @Test
    void testRecordTransactionSent() {
        // Arrange
        when(userService.getUserIdByUsername(testUsername)).thenReturn(userId);
        when(userService.getEmailByUsername(testUsername)).thenReturn("test@example.com");
        when(userService.getEmailByUsername(receiverUsername)).thenReturn("receiver@example.com");
        when(transactionRepository.save(transactionSent)).thenReturn(transactionSent);

        // Act
        TransactionDTO transactionDTO = transactionService.recordTransaction(testUsername, transferAmount, TransactionENUM.SENT, receiverId, receiverUsername, 0);

        // Assert
        assertNotNull(transactionDTO);
        assertEquals(transactionDTO.getAmount(), transactionSent.getAmount());
        verify(emailService).sendTransferEmail("receiver@example.com", testUsername, "test@example.com", receiverUsername, transferAmount);
    }

    @Test
    void testRecordTransactionUserNotFound() {
        // Arrange
        when(userService.getUserIdByUsername(testUsername)).thenReturn(null);

        // Act & Assert
        WalletException exception = assertThrows(WalletException.class, () -> {
            transactionService.recordTransaction(testUsername, rechargeAmount, TransactionENUM.RECHARGE, null, null, 0);
        });
        assertEquals("User not found for username: " + testUsername, exception.getMessage());
    }

    @Test
    void testGetTransactionsByUserId() {
        // Arrange
        when(transactionRepository.findByUserId(userId)).thenReturn(Collections.singletonList(transaction));
        when(userService.getUserIdByUsername(testUsername)).thenReturn(userId);

        // Act
        List<TransactionDTO> transactions = transactionService.getTransactionsByUsername(testUsername);

        // Assert
        assertEquals(1, transactions.size());
        assertEquals(transaction.getAmount(), transactions.get(0).getAmount());
    }

    @Test
    void testGetTransactionById() {
        // Arrange
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        // Act
        TransactionDTO transactionDTO = transactionService.getTransactionById(transactionId);

        // Assert
        assertNotNull(transactionDTO);
        assertEquals(transaction.getAmount(), transactionDTO.getAmount());
    }

    @Test
    void testGetTransactionByIdNotFound() {
        // Arrange
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        WalletException exception = assertThrows(WalletException.class, () -> {
            transactionService.getTransactionById(transactionId);
        });
        assertEquals("Transaction not found for ID: " + transactionId, exception.getMessage());
    }

    @Test
    void testGetCashbackByUsername() {
        // Arrange
        when(userService.getUserIdByUsername(testUsername)).thenReturn(userId);
        when(transactionRepository.findByUserId(userId)).thenReturn(Collections.singletonList(transactionCashback));

        // Act
        List<TransactionDTO> cashbacks = transactionService.getCashbackByUsername(testUsername);

        // Assert
        assertEquals(1, cashbacks.size());
        assertEquals(transactionCashback.getAmount(), cashbacks.get(0).getAmount());
    }

    @Test
    void testGetCashbackByUsernameUserNotFound() {
        // Arrange
        when(userService.getUserIdByUsername(testUsername)).thenReturn(null);

        // Act & Assert
        WalletException exception = assertThrows(WalletException.class, () -> {
            transactionService.getCashbackByUsername(testUsername);
        });
        assertEquals("User not found for username: " + testUsername, exception.getMessage());
    }
}
