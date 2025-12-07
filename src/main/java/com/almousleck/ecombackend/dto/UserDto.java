package com.almousleck.ecombackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isVerified;
    private String otp;
    private String otpGeneratedTime;
    private int failedLoginAttempts;
    private boolean isAccountLocked;
    private String accountLockTime;
    private List<OrderDto> orders;
    private CartDto cart;
}
