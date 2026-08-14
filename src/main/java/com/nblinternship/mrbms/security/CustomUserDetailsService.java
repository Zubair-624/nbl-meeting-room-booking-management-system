package com.nblinternship.mrbms.security;

import com.nblinternship.mrbms.entity.User;
import com.nblinternship.mrbms.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<SimpleGrantedAuthority> authorities = Collections.emptyList();

        if (user.getUserRoles() != null && !user.getUserRoles().isEmpty()) {
            authorities = user.getUserRoles().stream()
                    .map(ur -> {
                        String roleName = ur.getRole().getRoleName().toUpperCase(); // <-- Normalized to UPPERCASE
                        if (!roleName.startsWith("ROLE_")) {
                            roleName = "ROLE_" + roleName;
                        }
                        return new SimpleGrantedAuthority(roleName);
                    })
                    .collect(Collectors.toList());
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .disabled(!Boolean.TRUE.equals(user.getStatus()))
                .authorities(authorities)
                .build();
    }
}