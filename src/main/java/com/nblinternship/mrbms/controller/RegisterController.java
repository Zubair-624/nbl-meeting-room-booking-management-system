package com.nblinternship.mrbms.controller;

import com.nblinternship.mrbms.dto.RegisterRequest;
import com.nblinternship.mrbms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    //@GetMapping("/register") runs → shows you the empty register.html form
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    //You fill in all 8 fields and click "Sign Up"
    //Browser sends a POST request to /register with your form data

    //Your Controller's @PostMapping("/register") receives it
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(registerRequest);
        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.reject("registrationError", e.getMessage());
            return "register";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
        return "redirect:/login";
    }
}