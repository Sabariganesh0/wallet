package com.mainproject.wallet.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    @BeforeEach
    void setUp() throws MessagingException {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(mimeMessageHelper.getMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSend_WhenSendingRechargeRechargeEmail_Success() throws MessagingException {
        String to = "user@example.com";
        double amount = 100.0;
        double cashback = 10.0;

        emailService.sendRechargeEmail(to, amount, cashback);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSend_WhenTransferEmail_Success() throws MessagingException {
        String recipient = "recipient@example.com";
        String sender = "sender@example.com";
        double amount = 50.0;

        emailService.sendTransferEmail(recipient, sender, sender, recipient, amount);

        verify(mailSender, times(2)).send(mimeMessage);
    }
}