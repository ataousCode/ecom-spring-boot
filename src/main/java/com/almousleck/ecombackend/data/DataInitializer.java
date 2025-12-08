package com.almousleck.ecombackend.data;

import com.almousleck.ecombackend.role.Role;
import com.almousleck.ecombackend.role.RoleRepository;
import com.almousleck.ecombackend.user.User;
import com.almousleck.ecombackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createDefaultRoles();
        createAdminAccount();
        createUserAccount();
    }

    //Helpers method
    private void createDefaultRoles() {
        Set<String> defaultRoles = Set.of("ROLE_ADMIN", "ROLE_USER");
        defaultRoles.stream()
                .filter(role -> roleRepository.findByName(role).isEmpty())
                .map(Role::new)
                .forEach(roleRepository::save);
    }

    private void createAdminAccount() {
        String adminEmail = "almouslecka@gmail.com";
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
        User admin = new User();
        admin.setFirstName("Almousleck");
        admin.setLastName("Atalib Ag");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRoles(Set.of(adminRole));
        admin.setVerified(true);
        admin.setAccountLocked(false);
        admin.setFailedLoginAttempts(0);
        userRepository.save(admin);
    }

    private void createUserAccount() {
        String userEmail = "lamine@gmail.com";
        if (userRepository.existsByEmail(userEmail)) {
            return;
        }

        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
        User user = new User();
        user.setFirstName("Lamine");
        user.setLastName("Ag");
        user.setEmail(userEmail);
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Set.of(userRole));
        user.setVerified(true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }
}
