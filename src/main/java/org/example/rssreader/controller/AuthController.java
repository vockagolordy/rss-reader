package org.example.rssreader.controller;

import org.example.rssreader.dto.UserRegistrationDto;
import org.example.rssreader.model.User;
import org.example.rssreader.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        if (userService.findByUsername(userDto.getUsername()).isPresent()) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "register";
        }

        if (userService.findByEmail(userDto.getEmail()).isPresent()) {
            result.rejectValue("email", "error.user", "Email already exists");
            return "register";
        }

        userService.register(userDto);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "registered", required = false) String registered,
                                Model model, HttpSession session) {

        if (session.getAttribute("user") != null) {
            return "redirect:/posts";
        }

        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        if (registered != null) {
            model.addAttribute("message", "Registration successful! Please login.");
        }
        return "login";
    }
}