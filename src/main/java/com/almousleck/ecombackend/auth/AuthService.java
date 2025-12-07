package com.almousleck.ecombackend.auth;

import com.almousleck.ecombackend.request.LoginRequest;
import com.almousleck.ecombackend.request.ResetPasswordRequest;
import com.almousleck.ecombackend.response.JwtResponse;
import jakarta.mail.MessagingException;

public interface AuthService {
    JwtResponse login(LoginRequest request);
    void forgotPassword(String email) throws MessagingException;
    void resetPassword(ResetPasswordRequest request);
}
