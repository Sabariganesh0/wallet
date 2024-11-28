package com.mainproject.wallet.service;

import com.mainproject.wallet.exception.WalletException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Constructor injection
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendRechargeEmail(String to, double amount, double cashback) {
        String subject = "Recharge Successful";
        String content = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background: linear-gradient(to right, #4A90E2, #5B9BD5); padding: 20px; }" +
                ".container { max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }" +
                "h1 { color: #4A90E2; }" +
                "p { font-size: 16px; line-height: 1.5; }" +
                "strong { color: #333; }" +
                ".logo { width: 50px; height: auto; max-height: 50px; vertical-align: middle; }" + // Set height to auto
                ".header { display: flex; align-items: center; justify-content: flex-start;  width: 100%; margin: 0; padding: 0;  }"+
                ".header h2 { margin-left: 10px; color: #b82468; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<img src='https://media.licdn.com/dms/image/v2/C560BAQH0BXGtmrAFpw/company-logo_200_200/company-logo_200_200/0/1631332095782?e=2147483647&v=beta&t=T-8IKplgMaO2uFBLNh19LEzR9Xc8hzuP4hsc8Ifik9g' alt='Logo' class='logo'>" +
                "<h2>Tuple Pay</h2>" +
                "</div>" +
                "<h1>Your Wallet Has Been Recharged!</h1>" +
                "<p>Your wallet has been recharged with <strong>₹" + amount + "</strong>.</p>";

                if(cashback>0)
                {
                    content+="<p>You have earned a cashback of <strong>₹" + cashback + "</strong>.</p>";
                }

                content+=
                "<p>Thank you for using our service!</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendEmail(to, subject, content);
    }

    @Async
    public void sendTransferEmail(String toMail, String from,String fromMail,String to , double amount) {
        String subject = "Transfer Notification";
        String contentForRecipient = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background: linear-gradient(to right, #4A90E2, #5B9BD5); padding: 20px; }" +
                ".container { max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }" +
                "h1 { color: #4A90E2; }" +
                "p { font-size: 16px; line-height: 1.5; }" +
                "strong { color: #333; }" +
                ".logo { width: 50px; height: auto; max-height: 50px; vertical-align: middle; }" +
                ".header { display: flex; align-items: center; justify-content: flex-start;  width: 100%; margin: 0; padding: 0;  }" +
                ".header h2 { margin-left: 10px; color: #b82468; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<img src='https://media.licdn.com/dms/image/v2/C560BAQH0BXGtmrAFpw/company-logo_200_200/company-logo_200_200/0/1631332095782?e=2147483647&v=beta&t=T-8IKplgMaO2uFBLNh19LEzR9Xc8hzuP4hsc8Ifik9g' alt='Logo' class='logo'>" +
                "<h2>Tuple Pay</h2>" +
                "</div>" +
                "<h1>Transfer Received!</h1>" +
                "<p>You have received <strong>₹" + amount + "</strong> from <strong>" + from + "</strong>.</p>" +
                "<p>Thank you for using our service!</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        String contentForSender = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background: linear-gradient(to right, #4A90E2, #5B9BD5); padding: 20px; }" +
                ".container { max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }" +
                "h1 { color: #4A90E2; }" +
                "p { font-size: 16px; line-height: 1.5; }" +
                "strong { color: #333; }" +
                ".logo { width: 50px; height: auto; max-height: 50px; vertical-align: middle; }" +
                ".header { display: flex; align-items: center; justify-content: flex-start;  width: 100%; margin: 0; padding: 0;  }" +
                ".header h2 { margin-left: 10px; color: #b82468; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<img src='https://media.licdn.com/dms/image/v2/C560BAQH0BXGtmrAFpw/company-logo_200_200/company-logo_200_200/0/1631332095782?e=2147483647&v=beta&t=T-8IKplgMaO2uFBLNh19LEzR9Xc8hzuP4hsc8Ifik9g' alt='Logo' class='logo'>" +
                "<h2>Tuple Pay</h2>" +
                "</div>" +
                "<h1>Transfer Sent!</h1>" +
                "<p>You have sent <strong>₹" + amount + "</strong> to <strong>" + to + "</strong>.</p>" +
                "<p>Thank you for using our service!</p>" +
                "</div>" +
                "</body>" +
                "</html>";


        log.info("Sending email to: {}", toMail);
        log.info("Sending email from: {}", fromMail);

        // Send email to recipient
        sendEmail(toMail, subject, contentForRecipient);

        // Send email to sender
        sendEmail(fromMail, subject, contentForSender);
    }




    private void sendEmail(String to, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // Set to true to send as HTML
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to); // Log success
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage()); // Loggin error
        }
    }

}
