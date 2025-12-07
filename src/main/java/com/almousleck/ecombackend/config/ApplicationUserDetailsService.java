package com.almousleck.ecombackend.config;

import com.almousleck.ecombackend.otp.LoginAttemptService;
import com.almousleck.ecombackend.user.User;
import com.almousleck.ecombackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (loginAttemptService.isAccountLocked(email))
            throw new LockedException("Account is locked due to too many failed login attempts.");

        User user = userRepository.findByEmail(email);

        if (user == null)
            throw new UsernameNotFoundException("User not found with email: " + email);

        return ApplicationUserDetails.buildApplicationDetails(user);
    }
}
