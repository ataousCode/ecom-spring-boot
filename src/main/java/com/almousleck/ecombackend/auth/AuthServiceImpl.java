package com.almousleck.ecombackend.auth;

import com.almousleck.ecombackend.config.ApplicationUserDetails;
import com.almousleck.ecombackend.email.EmailService;
import com.almousleck.ecombackend.exception.ResourceNotFoundException;
import com.almousleck.ecombackend.jwt.JwtUtils;
import com.almousleck.ecombackend.request.LoginRequest;
import com.almousleck.ecombackend.request.ResetPasswordRequest;
import com.almousleck.ecombackend.response.JwtResponse;
import com.almousleck.ecombackend.user.User;
import com.almousleck.ecombackend.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateTokenForUser(authentication);
        ApplicationUserDetails userDetails = (ApplicationUserDetails) authentication.getPrincipal();
        JwtResponse response = new JwtResponse();
        response.setId(userDetails.getId());
        response.setToken(jwt);
        return response;
    }

    @Override
    public void forgotPassword(String email) throws MessagingException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(email, token);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken());
        if (user == null || user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Invalid or expired token.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);
    }
}
