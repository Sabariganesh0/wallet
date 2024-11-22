package com.mainproject.wallet.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "1234xyz";
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour
    private final long CLOCK_SKEW = 1000 * 60; // Allow 1 minute skew

    // Generate a token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Extract username from token
    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token.replace("Bearer ", "")) // Remove "Bearer " prefix
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null; // Invalid token
        }
    }

    // Validate the token
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        // Check if extractedUsername is null or if it doesn't match the provided username
        if (extractedUsername == null) {
            return false; // Invalid token
        }
        return !isTokenExpired(token);
    }

    // Check if the token is expired with clock skew
    boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date(System.currentTimeMillis() - CLOCK_SKEW));
    }

    // Extract expiration date
    Date extractExpiration(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody()
                .getExpiration();
    }
}
