package com.ticketsystem.zimsmartvillages.repository;

import com.ticketsystem.zimsmartvillages.model.Role;
import com.ticketsystem.zimsmartvillages.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    List<User> findByRoles(Set<Role> roles);
}
