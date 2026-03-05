package com.example.ecommercephone.controller;

import com.example.ecommercephone.dto.request.RegisterRequest;
import com.example.ecommercephone.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/login")
    public String login() { return "auth/login"; }
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid RegisterRequest registerRequest, 
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            accountService.register(registerRequest);
            redirectAttributes.addAttribute("success", true);
            return "redirect:/login?success=true";
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/register?error=" + e.getMessage();
        }
    }
}


