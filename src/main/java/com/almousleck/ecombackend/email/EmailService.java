package com.almousleck.ecombackend.email;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendOtpEmail(String to, String otp) throws MessagingException;
}
