package com.mainproject.wallet.service;

import com.mainproject.wallet.dto.RechargeResponseDTO;
import com.mainproject.wallet.exception.WalletException;
import com.mainproject.wallet.model.User;
import com.mainproject.wallet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserService userService;

    // Constants for test values
    private static final String USERNAME = "testUser";
    private static final String USER_ID = "1";
    private static final double INITIAL_BALANCE = 200.0;
    private static final double RECHARGE_AMOUNT = 100.0;
    private static final double INSUFFICIENT_AMOUNT = 500.0;
    private static final String TO_USERNAME = "receiverUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        walletService.cashbackLowerPercent = 5.0;
        walletService.cashbackUpperPercent = 10.0;
    }

    @Test
    void testRecharge_Success_returnsRechargeResponse() {
        User user = createUser(USER_ID, INITIAL_BALANCE);

        when(userService.getUserIdByUsername(USERNAME)).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        ResponseEntity<RechargeResponseDTO> response = walletService.recharge(USERNAME, RECHARGE_AMOUNT);

        assertNotNull(response);
        RechargeResponseDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(INITIAL_BALANCE + RECHARGE_AMOUNT + responseBody.getCashbackAmount(), user.getWalletBalance());
        assertTrue(responseBody.getCashbackAmount() >= 0); // Ensure cashback is calculated
    }

    @Test
    void testRecharge_UserNotFound_throwsWalletException() {
        when(userService.getUserIdByUsername(USERNAME)).thenReturn(null);

        WalletException exception = assertThrows(WalletException.class, () -> walletService.recharge(USERNAME, RECHARGE_AMOUNT));
        assertEquals("No user found for username: " + USERNAME, exception.getMessage());
    }

    @Test
    void testTransfer_Success_returnsUser() {
        User fromUser = createUser(USER_ID, INITIAL_BALANCE);
        User toUser = createUser("2", INITIAL_BALANCE, "receiverUser");

        when(userService.getUserIdByUsername(USERNAME)).thenReturn(USER_ID);
        when(userService.getUserIdByUsername(TO_USERNAME)).thenReturn("2");
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById("2")).thenReturn(Optional.of(toUser));

        User resultUser = walletService.transfer(USERNAME, TO_USERNAME, 50.0);

        assertEquals(TO_USERNAME, resultUser.getUsername());
        assertEquals(INITIAL_BALANCE - 50.0, fromUser.getWalletBalance());
        assertEquals(INITIAL_BALANCE + 50.0, toUser.getWalletBalance());
    }

    @Test
    void testTransfer_SameUser_throwsWalletException() {
        WalletException exception = assertThrows(WalletException.class, () -> walletService.transfer(USERNAME, USERNAME, 50.0));
        assertEquals("Cannot transfer money to yourself", exception.getMessage());
    }

    @Test
    void testTransfer_UserNotFound_throwsWalletException() {
        when(userService.getUserIdByUsername(USERNAME)).thenReturn(USER_ID);
        when(userService.getUserIdByUsername(TO_USERNAME)).thenReturn(null);

        WalletException exception = assertThrows(WalletException.class, () -> walletService.transfer(USERNAME, TO_USERNAME, 50.0));
        assertEquals("Wallet not found for username " + TO_USERNAME, exception.getMessage());
    }

    @Test
    void testTransfer_InsufficientFunds_throwsWalletException() {
        User fromUser = createUser(USER_ID, INITIAL_BALANCE);
        User toUser = createUser("2", INITIAL_BALANCE);

        when(userService.getUserIdByUsername(USERNAME)).thenReturn(USER_ID);
        when(userService.getUserIdByUsername(TO_USERNAME)).thenReturn("2");
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById("2")).thenReturn(Optional.of(toUser));

        WalletException exception = assertThrows(WalletException.class, () -> walletService.transfer(USERNAME, TO_USERNAME, INSUFFICIENT_AMOUNT));
        assertEquals("Insufficient funds in the account", exception.getMessage());
    }

    @Test
    void testViewStatement_Success_returnsUser() {
        User user = createUser(USER_ID, INITIAL_BALANCE);

        when(userService.getUserIdByUsername(USERNAME)).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        User resultUser = walletService.viewStatement(USERNAME);
        assertEquals(INITIAL_BALANCE, resultUser.getWalletBalance());
    }

    @Test
    void testViewStatement_UserNotFound_throwsWalletException() {
        when(userService.getUserIdByUsername(USERNAME)).thenReturn(null);

        WalletException exception = assertThrows(WalletException.class, () -> walletService.viewStatement(USERNAME));
        assertEquals("No user found for username: " + USERNAME, exception.getMessage());
    }

    // Helper method to create a User object
    private User createUser(String id, double walletBalance) {
        User user = new User();
        user.setId(id);
        user.setWalletBalance(walletBalance);
        return user;
    }

    // Overloaded helper method for creating a User with a username
    private User createUser(String id, double walletBalance, String username) {
        User user = createUser(id, walletBalance);
        user.setUsername(username);
        return user;
    }
}
