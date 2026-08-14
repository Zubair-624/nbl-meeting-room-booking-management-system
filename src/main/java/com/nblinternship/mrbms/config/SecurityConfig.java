package com.nblinternship.mrbms.config;

import com.nblinternship.mrbms.repository.UserRepository;
import com.nblinternship.mrbms.security.CustomUserDetailsService;
import com.nblinternship.mrbms.service.AuditService;
import com.nblinternship.mrbms.util.RequestUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.Objects;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler(AuditService auditService, UserRepository userRepository) {
        return (request, response, authentication) -> {

            String email = authentication.getName();
            userRepository.findByEmail(email).ifPresent(user ->
                    auditService.log(user.getUserId(), AuditService.ACTION_LOGIN, AuditService.MODULE_AUTH, RequestUtil.getClientIp())
            );

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));

            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else {
                response.sendRedirect("/employee/dashboard");
            }
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler(AuditService auditService, UserRepository userRepository) {
        return (request, response, authentication) -> {
            if (authentication != null) {
                String email = authentication.getName();
                userRepository.findByEmail(email).ifPresent(user ->
                        auditService.log(user.getUserId(), AuditService.ACTION_LOGOUT, AuditService.MODULE_AUTH, RequestUtil.getClientIp())
                );
            }
            response.sendRedirect("/login?logout");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationSuccessHandler successHandler,
                                                   LogoutSuccessHandler logoutSuccessHandler) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/login", "/tv/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**", "/users/**").hasRole("ADMIN")
                        .requestMatchers("/employee/**", "/api/calendar/**").hasAnyRole("EMPLOYEE", "ADMIN", "USER")
                        .anyRequest().authenticated()
                )
                .userDetailsService(customUserDetailsService)
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .permitAll()
                );

        return http.build();
    }
}