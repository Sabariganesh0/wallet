package com.mainproject.wallet.controller;

import com.mainproject.wallet.dto.ErrorResponseDTO;
import com.mainproject.wallet.dto.RechargeResponseDTO;
import com.mainproject.wallet.dto.TransactionDTO;
import com.mainproject.wallet.dto.UserDTO;
import com.mainproject.wallet.mapper.UserMapper;
import com.mainproject.wallet.service.TransactionService;
import com.mainproject.wallet.service.WalletService;
import com.mainproject.wallet.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletControllerTest {

    @InjectMocks
    private WalletController walletController;

    @Mock
    private WalletService walletService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private JwtUtil jwtUtil;

    // Reused variables
    private final String token = "Bearer validToken";
    private final String username = "testUser";
    private final double validAmount = 100.0;
    private final double zeroAmount = 0.0;
    private final String receiverUsername = "receiverUser";
    private UserDTO expectedUserDTO;
    private RechargeResponseDTO rechargeResponse;
    private List<TransactionDTO> transactions;
    private List<TransactionDTO> cashbacks;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize reused variables
        expectedUserDTO = new UserDTO("1", username, "email@example.com", 0.0, token);
        rechargeResponse = new RechargeResponseDTO(10.0, validAmount);

        transactions = new ArrayList<>();
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId("1");
        transactionDTO.setUserId(username);
        transactionDTO.setAmount(validAmount);
        transactions.add(transactionDTO);

        cashbacks = new ArrayList<>();
        TransactionDTO cashbackDTO = new TransactionDTO();
        cashbackDTO.setId("2");
        cashbackDTO.setUserId(username);
        cashbackDTO.setAmount(20.0);
        cashbacks.add(cashbackDTO);
    }

    @Test
    void testRecharge_ShouldReturnRechargeResponse_WhenAmountIsValid() {
        // Arrange
        when(jwtUtil.extractUsername(any(String.class))).thenReturn(username);
        when(jwtUtil.isTokenValid(any(String.class),any(String.class))).thenReturn(true);
        when(walletService.recharge(username, validAmount)).thenReturn(ResponseEntity.ok(rechargeResponse));

        // Act
        ResponseEntity<?> response = walletController.recharge(token, validAmount);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(rechargeResponse, response.getBody());
    }

    @Test
    void testRecharge_ShouldReturnBadRequest_WhenAmountIsZeroOrNegative() {
        // Act
        ResponseEntity<?> response = walletController.recharge(token, zeroAmount);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertEquals(new ErrorResponseDTO("Unauthorized access"), response.getBody());
    }

    @Test
    void testRecharge_ShouldReturnUnauthorized_WhenTokenIsInvalid() {
        // Arrange
        when(jwtUtil.extractUsername(any(String.class))).thenReturn(null); // Simulate invalid token

        // Act
        ResponseEntity<?> response = walletController.recharge(token, validAmount);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertEquals(new ErrorResponseDTO("Unauthorized access"), response.getBody());
    }

    @Test
    void testTransfer_ShouldReturnUserDTO_WhenTransferIsSuccessful() {
        // Arrange
        when(jwtUtil.extractUsername(any(String.class))).thenReturn(username);
        when(jwtUtil.isTokenValid(any(String.class),any(String.class))).thenReturn(true);
        when(walletService.transfer(username, receiverUsername, validAmount)).thenReturn(UserMapper.toEntity(expectedUserDTO));

        // Act
        ResponseEntity<?> response = walletController.transfer(token, receiverUsername, validAmount);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedUserDTO, response.getBody());
    }

    @Test
    void testTransfer_ShouldReturnBadRequest_WhenAmountIsZeroOrNegative() {
        // Act
        ResponseEntity<?> response = walletController.transfer(token, receiverUsername, zeroAmount);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertEquals(new ErrorResponseDTO("Unauthorized access"), response.getBody());
    }

    @Test
    void testTransfer_ShouldReturnUnauthorized_WhenTokenIsInvalid() {
        // Arrange
        when(jwtUtil.extractUsername(any(String.class))).thenReturn(null); // Simulate invalid token

        // Act
        ResponseEntity<?> response = walletController.transfer(token, receiverUsername, validAmount);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertEquals(new ErrorResponseDTO("Unauthorized access"), response.getBody());
    }

    @Test
    void testViewStatement_ShouldReturnUserDTO_WhenSuccessful() {
        // Arrange
        when(jwtUtil.extractUsername(any(String.class))).thenReturn(username);
        when(jwtUtil.isTokenValid(any(String.class),any(String.class))).thenReturn(true);
        when(walletService.viewStatement(username)).thenReturn(UserMapper.toEntity(expectedUserDTO));

        // Act
        ResponseEntity<?> response = walletController.viewStatement(token);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedUserDTO, response.getBody());
    }

    @Test
    void testViewStatement_ShouldReturnUnauthorized_WhenTokenIsInvalid() {
        // Arrange
        when(jwtUtil.extractUsername(any(String.class))).thenReturn(null); // Simulate invalid token

        // Act
        ResponseEntity<?> response = walletController.viewStatement(token);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertEquals(new ErrorResponseDTO("Unauthorized access"), response.getBody());
    }

    @Test
    void testGetTransactions_ShouldReturnTransactionList() {
        // Arrange
        when(jwtUtil.extractUsername(any(String.class))).thenReturn(username);
        when(jwtUtil.isTokenValid(any(String.class),any(String.class))).thenReturn(true);
        when(transactionService.getTransactionsByUsername(username)).thenReturn(transactions);

        // Act
        ResponseEntity<?> response = walletController.getTransactions(token, username);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(transactions, response.getBody());
    }

    @Test
    void testGetTransactions_ShouldReturnUnauthorized_WhenTokenIsInvalid() {
        // Arrange
        when(jwtUtil.extractUsername(any(String.class))).thenReturn(null); // Simulate invalid token

        // Act
        ResponseEntity<?> response = walletController.getTransactions(token, username);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertEquals(new ErrorResponseDTO("Unauthorized access"), response.getBody());
    }

    @Test
    void testGetAllCashbacks_ShouldReturnCashbackList() {
        // Arrange
        when(jwtUtil.extractUsername(any(String.class))).thenReturn(username);
        when(jwtUtil.isTokenValid(any(String.class),any(String.class))).thenReturn(true);
        when(transactionService.getCashbackByUsername(username)).thenReturn(cashbacks);

        // Act
        ResponseEntity<?> response = walletController.getAllCashbacks(token, username);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(cashbacks, response.getBody());
    }

    @Test
    void testGetAllCashbacks_ShouldReturnUnauthorized_WhenTokenIsInvalid() {
        // Arrange
        when(jwtUtil.extractUsername(any(String.class))).thenReturn(null); // Simulate invalid token

        // Act
        ResponseEntity<?> response = walletController.getAllCashbacks(token, username);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertEquals(new ErrorResponseDTO("Unauthorized access"), response.getBody());
    }
}
