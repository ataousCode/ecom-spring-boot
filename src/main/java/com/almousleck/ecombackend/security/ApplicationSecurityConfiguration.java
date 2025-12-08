package com.almousleck.ecombackend.security;

import com.almousleck.ecombackend.config.ApplicationUserDetailsService;
import com.almousleck.ecombackend.jwt.AuthenticationTokenFilter;
import com.almousleck.ecombackend.jwt.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@EnableMethodSecurity
public class ApplicationSecurityConfiguration {

    private final ApplicationUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final SecurityConfig securityConfig;

    private static final String[] PUBLIC_GETS = {
            "/api/v1/products/**",
            "/api/v1/categories/**",
            "/api/v1/images/**"
    };

    private static final String[] ADMIN_ONLY = {
            "/api/v1/carts/**",
            "/api/v1/cartItems/**",
            "/api/v1/products/**",    // POST/PUT/DELETE will be restricted below
            "/api/v1/categories/**",
            "/api/v1/images/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationTokenFilter authenticationTokenFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ADMIN_ONLY).hasRole("ADMIN")
                        .anyRequest().permitAll());
        http.authenticationProvider(securityConfig.daoAuthenticationProvider(userDetailsService));
        http.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
