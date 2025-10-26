package com.sliit.vaccinationportal.controller.IT24103103;

import com.sliit.vaccinationportal.model.User;
import com.sliit.vaccinationportal.service.IT24103103.EmailService;
import com.sliit.vaccinationportal.service.IT24103103.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String loginPage() {
        return "IT24103103/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "IT24103103/register";
    }

    @PostMapping("/register/save")
    public String registration(@ModelAttribute("user") User user, Model model) {
        String password = user.getPassword();
        // Password validation: min 8 chars, at least one digit, one uppercase
        if (password == null || !password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            model.addAttribute("error", "Password must be at least 8 characters, contain an uppercase letter and a number.");
            return "IT24103103/register";
        }
        // Birthday validation: must be before today
        if (user.getBirthday() == null || !user.getBirthday().isBefore(LocalDate.now())) {
            model.addAttribute("error", "Birthday must be before today.");
            return "IT24103103/register";
        }
        userService.saveUser(user);
        return "redirect:/register?success";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "IT24103103/forgot-password-form";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email) {
        Optional<User> optionalUser = userService.findUserByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            userService.createPasswordResetTokenForUser(user);
            String resetUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/reset-password")
                    .queryParam("token", user.getResetPasswordToken())
                    .build()
                    .toUriString();
            emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
        }
        return "redirect:/forgot-password?success";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<User> userOptional = userService.getUserByPasswordResetToken(token);
        if (userOptional.isEmpty() || userOptional.get().getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            return "redirect:/login?invalidToken";
        }
        model.addAttribute("token", token);
        return "IT24103103/reset-password-form";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password) {
        Optional<User> userOptional = userService.getUserByPasswordResetToken(token);
        if (userOptional.isEmpty() || userOptional.get().getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            return "redirect:/login?invalidToken";
        }
        // Basic server-side password policy (mirrors registration)
        if (password == null || !password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            return "redirect:/reset-password?token=" + token + "&weak";
        }
        User user = userOptional.get();
        userService.changeUserPassword(user, password);
        return "redirect:/login?resetSuccess";
    }
}