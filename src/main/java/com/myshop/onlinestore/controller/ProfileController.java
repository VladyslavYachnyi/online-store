package com.myshop.onlinestore.controller;

import com.myshop.onlinestore.entity.User;
import com.myshop.onlinestore.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("currentUserEmail", principal.getName());

            Optional<User> userOpt = userService.getUserByEmail(principal.getName());
            if (userOpt.isPresent()) {
                model.addAttribute("user", userOpt.get());
            } else {
                model.addAttribute("user", new User());
            }
        }
        return "profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute User updatedUser, Principal principal) {
        Optional<User> userOpt = userService.getUserByEmail(principal.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setUsername(updatedUser.getUsername());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            user.setLocation(updatedUser.getLocation());

            userService.updateProfile(user);
        }
        return "redirect:/profile";
    }
}