package com.almousleck.ecombackend.contoller;

import com.almousleck.ecombackend.config.ApplicationUserDetails;
import com.almousleck.ecombackend.jwt.JwtUtils;
import com.almousleck.ecombackend.request.LoginRequest;
import com.almousleck.ecombackend.response.ApiResponse;
import com.almousleck.ecombackend.response.JwtResponse;
import com.almousleck.ecombackend.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
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
            return ResponseEntity.ok(new ApiResponse("Login success", response));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(ex.getMessage(), null));
        }
    }
}
