package com.ticketsystem.zimsmartvillages.service;

import com.ticketsystem.zimsmartvillages.exception.ResourceNotFoundException;
import com.ticketsystem.zimsmartvillages.model.Role;
import com.ticketsystem.zimsmartvillages.model.User;
import com.ticketsystem.zimsmartvillages.repository.RoleRepository;
import com.ticketsystem.zimsmartvillages.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User updateUser(Long id, String username, String email, String fullName, Set<Role> roles) {
        User user = getUserById(id);

        // Check if the new username already exists and it's not the current user
        if (!user.getUsername().equals(username) && existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken");
        }

        // Check if the new email already exists and it's not the current user
        if (!user.getEmail().equals(email) && existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use");
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);

        if (roles != null && !roles.isEmpty()) {
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public User resetPassword(Long id, String newPassword) {
        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User updateUserRole(Long userId, Long roleId) {
        User user = getUserById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        Set<Role> roles = user.getRoles();
        roles.add(role);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public User removeUserRole(Long userId, Long roleId) {
        User user = getUserById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        Set<Role> roles = user.getRoles();
        roles.remove(role);
        user.setRoles(roles);

        return userRepository.save(user);
    }
}