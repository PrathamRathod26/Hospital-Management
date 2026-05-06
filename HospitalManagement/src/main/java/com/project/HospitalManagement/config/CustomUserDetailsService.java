package com.project.HospitalManagement.config;

import com.project.HospitalManagement.enums.Role;
import com.project.HospitalManagement.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String compositeKey) throws UsernameNotFoundException {
        String[] parts = compositeKey.split("\\|");
        if (parts.length < 2) {
            throw new UsernameNotFoundException("Invalid login format. Role missing.");
        }

        String email = parts[0];
        String roleStr = parts[1];
        Role role = Role.valueOf(roleStr);
        return userRepository.findByEmailAndRole(email, role)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("%s profile not found for email: %s", roleStr, email)
                ));
    }
}
