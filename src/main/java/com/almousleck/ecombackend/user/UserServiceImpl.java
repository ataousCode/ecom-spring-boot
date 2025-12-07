package com.almousleck.ecombackend.user;

import com.almousleck.ecombackend.dto.UserDto;
import com.almousleck.ecombackend.email.EmailService;
import com.almousleck.ecombackend.exception.AlreadyExistsException;
import com.almousleck.ecombackend.exception.ResourceNotFoundException;
import com.almousleck.ecombackend.otp.OtpUtil;
import com.almousleck.ecombackend.request.CreateUserRequest;
import com.almousleck.ecombackend.request.UserUpdateRequest;
import com.almousleck.ecombackend.role.Role;
import com.almousleck.ecombackend.role.RoleRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final OtpUtil otpUtil;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User with id " + userId + " not found")
                );
    }

    @Override
    public User createUser(CreateUserRequest request) {
        return  Optional.of(request)
                .filter(user -> !userRepository.existsByEmail(request.getEmail()))
                .map(req -> {
                    Role role = roleRepository.findByName("ROLE_USER")
                            .orElseThrow(
                                    () -> new ResourceNotFoundException("Role not found!")
                            );
                    User user = new User();
                    user.setEmail(request.getEmail());
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    user.setRoles(Set.of(role));

                    String otp = otpUtil.generateOtp();
                    user.setOtp(otp);
                    user.setOtpGeneratedTime(LocalDateTime.now());
                    try {
                        emailService.sendOtpEmail(user.getEmail(), otp);
                    } catch (MessagingException e) {
                        throw new RuntimeException("Unable to send OTP email please try again");
                    }
                    return  userRepository.save(user);
                }) .orElseThrow(() -> new AlreadyExistsException("Oops!" +request.getEmail() +" already exists!"));
    }
    @Override
    public User updateUser(UserUpdateRequest request, Long userId) {
        return  userRepository.findById(userId).map(existingUser ->{
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(userRepository :: delete, () ->{
            throw new ResourceNotFoundException("User not found!");
        });
    }

    @Override
    public UserDto convertUserToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public void verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email);

        if (user == null) throw new ResourceNotFoundException("User not found!");
        if (user.isVerified()) throw new AlreadyExistsException("User with email " + email + " already verified!");
        if (user.getOtp() == null || !user.getOtp().equals(otp)) throw new ResourceNotFoundException("Invalid OTP!");

        if (user.getOtpGeneratedTime().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalStateException("OTP has expired.");
        }

        user.setVerified(true);
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }
}
