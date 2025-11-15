package com.almousleck.ecombackend.user;

import com.almousleck.ecombackend.dto.UserDto;
import com.almousleck.ecombackend.request.CreateUserRequest;
import com.almousleck.ecombackend.request.UserUpdateRequest;

public interface UserService {
    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UserUpdateRequest request, Long userId);
    void deleteUser(Long userId);
    UserDto convertUserToDto(User user);
    User getAuthenticatedUser();
}
