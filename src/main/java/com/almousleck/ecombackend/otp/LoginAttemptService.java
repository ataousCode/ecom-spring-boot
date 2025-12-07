package com.almousleck.ecombackend.otp;

import com.almousleck.ecombackend.user.User;
import com.almousleck.ecombackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    private final UserRepository userRepository;

    public void loginSucceeded(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user.setFailedLoginAttempts(0);
            user.setAccountLocked(false);
            user.setAccountLockedTime(null);
            userRepository.save(user);
        }
    }

    public void loginFailed(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= MAX_FAILED_LOGIN_ATTEMPTS) {
                user.setAccountLocked(true);
                user.setAccountLockedTime(LocalDateTime.now());
            }
            userRepository.save(user);
        }
    }

    public boolean isAccountLocked(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.isAccountLocked()) {
            if (user.getAccountLockedTime().plusMinutes(LOCK_DURATION_MINUTES).isAfter(LocalDateTime.now())) {
                return true;
            } else {
                user.setAccountLocked(false);
                user.setFailedLoginAttempts(0);
                user.setAccountLockedTime(null);
                userRepository.save(user);
                return false;
            }
        }
        return false;
    }
}
