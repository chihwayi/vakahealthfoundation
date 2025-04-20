package com.ticketsystem.zimsmartvillages.service;

import com.ticketsystem.zimsmartvillages.exception.ResourceNotFoundException;
import com.ticketsystem.zimsmartvillages.model.Role;
import com.ticketsystem.zimsmartvillages.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
    }

    public Role updateRole(Long id, String name) {
        Role role = getRoleById(id);

        // Check if the new role name already exists
        if (!role.getName().equals(name) && roleRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Role name already exists");
        }

        role.setName(name);
        return roleRepository.save(role);
    }
}
