package com.almousleck.ecombackend.user;

import com.almousleck.ecombackend.dto.UserDto;
import com.almousleck.ecombackend.exception.AlreadyExistsException;
import com.almousleck.ecombackend.exception.ResourceNotFoundException;
import com.almousleck.ecombackend.request.CreateUserRequest;
import com.almousleck.ecombackend.request.UserUpdateRequest;
import com.almousleck.ecombackend.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new ApiResponse("Create User Success!", userDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // get user by id:
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new ApiResponse("Get User Success!", userDto));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(ex.getMessage(), null));
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUser(
            @RequestBody UserUpdateRequest request,
            @PathVariable Long userId) {
        try {
            User user = userService.updateUser(request, userId);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new ApiResponse("Update User Success!", userDto));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(ex.getMessage(), null));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse("Delete User Success!", null));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(ex.getMessage(), null));
        }
    }
}
