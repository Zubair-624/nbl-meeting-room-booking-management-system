package com.nblinternship.mrbms.service.impl;

import com.nblinternship.mrbms.dto.AdminUserRequest;
import com.nblinternship.mrbms.dto.RegisterRequest;
import com.nblinternship.mrbms.entity.Role;
import com.nblinternship.mrbms.entity.User;
import com.nblinternship.mrbms.entity.UserRole;
import com.nblinternship.mrbms.repository.RoleRepository;
import com.nblinternship.mrbms.repository.UserRepository;
import com.nblinternship.mrbms.repository.UserRoleRepository;
import com.nblinternship.mrbms.service.AuditService;
import com.nblinternship.mrbms.service.UserService;
import com.nblinternship.mrbms.util.RequestUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserRoleRepository userRoleRepository,
                           PasswordEncoder passwordEncoder,
                           AuditService auditService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmployeeId(String employeeId) {
        return userRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void registerUser(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        if (userRepository.findByEmployeeId(request.getEmployeeId()).isPresent()) {
            throw new IllegalArgumentException("Employee ID is already registered");
        }

        User user = new User();
        user.setEmployeeId(request.getEmployeeId());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setDepartment(request.getDepartment());
        user.setDesignation(request.getDesignation());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        Role defaultRole = roleRepository.findByRoleName("EMPLOYEE")
                .or(() -> roleRepository.findByRoleName("ROLE_EMPLOYEE"))
                .orElseThrow(() -> new IllegalStateException("Default role 'EMPLOYEE' not found in database. Please seed initial roles."));

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(defaultRole);
        userRoleRepository.save(userRole);

        auditService.log(savedUser.getUserId(), AuditService.ACTION_USER_REGISTERED, AuditService.MODULE_AUTH, RequestUtil.getClientIp());
    }

    @Override
    public User createUserByAdmin(AdminUserRequest request, Integer adminUserId) {
        return null;
    }

    @Override
    public User updateUser(Integer userId, AdminUserRequest request, Integer adminUserId) {
        return null;
    }

    @Override
    public void setUserStatus(Integer userId, boolean active, Integer adminUserId) {

    }

    @Override
    public String resetPassword(Integer userId, Integer adminUserId) {
        return "";
    }
}