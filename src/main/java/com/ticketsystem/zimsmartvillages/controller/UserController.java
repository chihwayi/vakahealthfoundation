package com.ticketsystem.zimsmartvillages.controller;

import com.ticketsystem.zimsmartvillages.dto.*;
import com.ticketsystem.zimsmartvillages.model.Role;
import com.ticketsystem.zimsmartvillages.model.User;
import com.ticketsystem.zimsmartvillages.service.RoleService;
import com.ticketsystem.zimsmartvillages.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> userDtos = users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(convertToDto(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateRequest) {
        // Check if username already exists
        if (!userService.getUserById(id).getUsername().equals(updateRequest.getUsername())
                && userService.existsByUsername(updateRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Check if email already exists
        if (!userService.getUserById(id).getEmail().equals(updateRequest.getEmail())
                && userService.existsByEmail(updateRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        Set<Role> roles = new HashSet<>();
        if (updateRequest.getRoles() != null) {
            updateRequest.getRoles().forEach(roleName -> {
                Role role = roleService.getRoleByName(roleName);
                roles.add(role);
            });
        }

        User updatedUser = userService.updateUser(
                id,
                updateRequest.getUsername(),
                updateRequest.getEmail(),
                updateRequest.getFullName(),
                roles
        );

        return ResponseEntity.ok(convertToDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest resetRequest) {
        User user = userService.resetPassword(id, resetRequest.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
    }

    @PutMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addUserRole(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = userService.updateUserRole(userId, roleId);
        return ResponseEntity.ok(convertToDto(user));
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> removeUserRole(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = userService.removeUserRole(userId, roleId);
        return ResponseEntity.ok(convertToDto(user));
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList())
        );
    }
}
