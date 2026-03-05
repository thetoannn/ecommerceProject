package com.example.ecommercephone.controller.user;

import com.example.ecommercephone.dto.request.ProfileUpdateRequest;
import com.example.ecommercephone.entity.Profile;
import com.example.ecommercephone.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails principal,
                              @RequestParam(value = "edit", required = false, defaultValue = "false") boolean edit,
                              Model model) {
        Profile currentProfile = (Profile) model.getAttribute("currentProfile");
        model.addAttribute("editing", edit);

        if (edit && !model.containsAttribute("profileForm")) {
            model.addAttribute("profileForm", profileService.toUpdateRequest(currentProfile));
        }

        return "profile/view";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails principal,
                                @Valid ProfileUpdateRequest profileForm,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.profileForm", bindingResult);
            redirectAttributes.addFlashAttribute("profileForm", profileForm);
            return "redirect:/profile?edit=true";
        }

        profileService.updateProfile(principal.getUsername(), profileForm);

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công");
        return "redirect:/profile";
    }
}