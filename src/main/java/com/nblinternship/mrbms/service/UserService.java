package com.nblinternship.mrbms.service;

import com.nblinternship.mrbms.dto.AdminUserRequest;
import com.nblinternship.mrbms.dto.RegisterRequest;
import com.nblinternship.mrbms.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();
    Optional<User> getUserById(Integer id);
    Optional<User> getUserByEmployeeId(String employeeId);
    Optional<User> getUserByEmail(String email);
    long getUserCount();

    User saveUser(User user);
    void registerUser(RegisterRequest request);

    User createUserByAdmin(AdminUserRequest request, Integer adminUserId);
    User updateUser(Integer userId, AdminUserRequest request, Integer adminUserId);
    void setUserStatus(Integer userId, boolean active, Integer adminUserId);
    String resetPassword(Integer userId, Integer adminUserId);
}