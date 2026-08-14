package com.nblinternship.mrbms.controller;

import com.nblinternship.mrbms.dto.AdminUserRequest;
import com.nblinternship.mrbms.entity.User;
import com.nblinternship.mrbms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    private Integer currentAdminId(UserDetails userDetails) {
        return userService.getUserByEmail(userDetails.getUsername())
                .map(User::getUserId)
                .orElse(null);
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin-users-list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("userRequest", new AdminUserRequest());
        return "admin-user-form";
    }

    @PostMapping("/new")
    public String createUser(@Valid @ModelAttribute("userRequest") AdminUserRequest userRequest,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin-user-form";
        }
        try {
            User created = userService.createUserByAdmin(userRequest, currentAdminId(userDetails));
            redirectAttributes.addFlashAttribute("successMessage",
                    "User created successfully. Temporary password: " + created.getPasswordHash());
        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.reject("createError", e.getMessage());
            return "admin-user-form";
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Integer id, Model model,
                               RedirectAttributes redirectAttributes) {
        return userService.getUserById(id).map(user -> {
            AdminUserRequest req = new AdminUserRequest();
            req.setEmployeeId(user.getEmployeeId());
            req.setName(user.getName());
            req.setEmail(user.getEmail());
            req.setDepartment(user.getDepartment());
            req.setDesignation(user.getDesignation());
            req.setPhone(user.getPhone());
            model.addAttribute("userRequest", req);
            model.addAttribute("userId", id);
            return "admin-user-edit-form";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/admin/users";
        });
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable("id") Integer id,
                             @Valid @ModelAttribute("userRequest") AdminUserRequest userRequest,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", id);
            return "admin-user-edit-form";
        }
        try {
            userService.updateUser(id, userRequest, currentAdminId(userDetails));
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/status")
    public String toggleStatus(@PathVariable("id") Integer id,
                               @RequestParam("active") boolean active,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.setUserStatus(id, active, currentAdminId(userDetails));
            redirectAttributes.addFlashAttribute("successMessage",
                    active ? "User enabled." : "User disabled.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable("id") Integer id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            String tempPassword = userService.resetPassword(id, currentAdminId(userDetails));
            redirectAttributes.addFlashAttribute("successMessage",
                    "Password reset. New temporary password: " + tempPassword);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}