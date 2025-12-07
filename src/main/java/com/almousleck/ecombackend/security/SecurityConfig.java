package com.almousleck.ecombackend.security;

import com.almousleck.ecombackend.config.ApplicationUserDetailsService;
import com.almousleck.ecombackend.config.AuthenticationFailureListener;
import com.almousleck.ecombackend.config.AuthenticationSuccessEventListener;
import com.almousleck.ecombackend.jwt.AuthenticationTokenFilter;
import com.almousleck.ecombackend.otp.LoginAttemptService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration

public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilterBean() {
        return new AuthenticationTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(ApplicationUserDetailsService applicationUserDetailsService) {
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(applicationUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationFailureListener authenticationFailureListener(LoginAttemptService loginAttemptService) {
        return new AuthenticationFailureListener(loginAttemptService);
    }

    @Bean
    public AuthenticationSuccessEventListener authenticationSuccessEventListener(LoginAttemptService loginAttemptService) {
        return new AuthenticationSuccessEventListener(loginAttemptService);
    }
}
