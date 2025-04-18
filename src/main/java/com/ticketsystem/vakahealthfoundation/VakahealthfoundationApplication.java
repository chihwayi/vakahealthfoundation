package com.ticketsystem.vakahealthfoundation;

import com.ticketsystem.vakahealthfoundation.model.Role;
import com.ticketsystem.vakahealthfoundation.model.User;
import com.ticketsystem.vakahealthfoundation.repository.UserRepository;
import com.ticketsystem.vakahealthfoundation.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class VakahealthfoundationApplication {

	public static void main(String[] args) {
		SpringApplication.run(VakahealthfoundationApplication.class, args);
	}

	// This bean initializes some data when the application starts
	@Bean
	CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Create roles if they don't exist
			Role adminRole = roleRepository.findByName("ROLE_ADMIN")
					.orElseGet(() -> {
						Role role = new Role();
						role.setName("ROLE_ADMIN");
						return roleRepository.save(role);
					});

			Role userRole = roleRepository.findByName("ROLE_USER")
					.orElseGet(() -> {
						Role role = new Role();
						role.setName("ROLE_USER");
						return roleRepository.save(role);
					});

			Role supportRole = roleRepository.findByName("ROLE_SUPPORT")
					.orElseGet(() -> {
						Role role = new Role();
						role.setName("ROLE_SUPPORT");
						return roleRepository.save(role);
					});

			// Create an admin user if it doesn't exist
			if (userRepository.findByUsername("admin").isEmpty()) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("admin"));
				admin.setEmail("admin@example.com");
				admin.setFullName("System Administrator");
				Set<Role> roles = new HashSet<>();
				roles.add(adminRole);
				admin.setRoles(roles);
				userRepository.save(admin);
			}

			// Create a support user if it doesn't exist
			if (userRepository.findByUsername("support").isEmpty()) {
				User support = new User();
				support.setUsername("support");
				support.setPassword(passwordEncoder.encode("support"));
				support.setEmail("support@example.com");
				support.setFullName("Support User");
				Set<Role> roles = new HashSet<>();
				roles.add(supportRole);
				support.setRoles(roles);
				userRepository.save(support);
			}

			// Create a regular user if it doesn't exist
			if (userRepository.findByUsername("user").isEmpty()) {
				User user = new User();
				user.setUsername("user");
				user.setPassword(passwordEncoder.encode("user"));
				user.setEmail("user@example.com");
				user.setFullName("Regular User");
				Set<Role> roles = new HashSet<>();
				roles.add(userRole);
				user.setRoles(roles);
				userRepository.save(user);
			}
		};
	}

}
