package com.mainproject.wallet.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private final String USERNAME = "testUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateToken_ValidUserName_returnsToken() {
        String token = jwtUtil.generateToken(USERNAME);

        assertNotNull(token);
        assertTrue(token.startsWith("eyJ")); // Valid JWTs start with 'eyJ'
    }

    @Test
    void testExtractUsername_ValidToken_returnsUserName() {
        String token = jwtUtil.generateToken(USERNAME);
        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals(USERNAME, extractedUsername);
    }

    @Test
    void testExtractUsername_InvalidToken_returnsNull() {
        String invalidToken = "invalidToken";
        String extractedUsername = jwtUtil.extractUsername(invalidToken);

        assertNull(extractedUsername);
    }

    @Test
    void testIsTokenValid_ValidToken_returnsTrue() {
        String token = jwtUtil.generateToken(USERNAME);
        boolean isValid = jwtUtil.isTokenValid(token, USERNAME);

        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_InvalidToken_returnsFalse() {
        String invalidToken = "invalidToken";
        boolean isValid = jwtUtil.isTokenValid(invalidToken, USERNAME);

        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_ExpiredToken_returnsFalse() {
        String token = createExpiredToken(USERNAME);

        boolean isValid = jwtUtil.isTokenValid(token, USERNAME);
        assertFalse(isValid);
    }

    @Test
    void testIsTokenExpired_ValidToken_returnsFalse() {
        String token = jwtUtil.generateToken(USERNAME);
        boolean isExpired = jwtUtil.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void testExtractExpiration_ValidToken_returnsExpirationDate() {
        String token = jwtUtil.generateToken(USERNAME);
        Date expirationDate = jwtUtil.extractExpiration(token);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void testExtractExpiration_InvalidToken_throwsJwtException() {
        String invalidToken = "invalidToken";
        assertThrows(JwtException.class, () -> jwtUtil.extractExpiration(invalidToken));
    }

    // Helper method to create an expired token
    private String createExpiredToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000)) // Issued 10 seconds ago
                .setExpiration(new Date(System.currentTimeMillis() - 5000)) // Expired 5 seconds ago
                .signWith(SignatureAlgorithm.HS256, "1234xyz") // Use the same secret as in JwtUtil
                .compact();
    }
}
