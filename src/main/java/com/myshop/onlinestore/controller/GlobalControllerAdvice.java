package com.myshop.onlinestore.controller;

import com.myshop.onlinestore.entity.User;
import com.myshop.onlinestore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addUserInfo(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String email = authentication.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                model.addAttribute("currentUserEmail", email);
                model.addAttribute("currentUserRole", user.getRole().name());
            }
        }
    }
}
