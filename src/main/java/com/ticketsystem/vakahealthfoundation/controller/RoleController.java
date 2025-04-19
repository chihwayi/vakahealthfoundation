package com.ticketsystem.vakahealthfoundation.controller;

import com.ticketsystem.vakahealthfoundation.dto.MessageResponse;
import com.ticketsystem.vakahealthfoundation.dto.RoleDto;
import com.ticketsystem.vakahealthfoundation.model.Role;
import com.ticketsystem.vakahealthfoundation.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleDto> roleDtos = roles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleDtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(convertToDto(role));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDto roleDto) {
        Role updatedRole = roleService.updateRole(id, roleDto.getName());
        return ResponseEntity.ok(convertToDto(updatedRole));
    }

    private RoleDto convertToDto(Role role) {
        return new RoleDto(role.getId(), role.getName());
    }
}
