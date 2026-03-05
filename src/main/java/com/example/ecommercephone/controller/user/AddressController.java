package com.example.ecommercephone.controller.user;

import com.example.ecommercephone.dto.request.CreateAddressRequest;
import com.example.ecommercephone.entity.Address;
import com.example.ecommercephone.entity.Profile;
import com.example.ecommercephone.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public String list(@ModelAttribute("currentProfile") Profile profile, Model model) {
        if (profile == null) {
            return "redirect:/login";
        }
        List<Address> addresses = addressService.getAddressesByProfileId(profile.getId());
        model.addAttribute("addresses", addresses);
        return "address/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("addressRequest")) {
            model.addAttribute("addressRequest", new CreateAddressRequest());
        }
        return "address/form";
    }

    @PostMapping("/create")
    public String create(@AuthenticationPrincipal UserDetails principal,
                         @ModelAttribute("currentProfile") Profile profile,
                         @Valid @ModelAttribute("addressRequest") CreateAddressRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (profile == null) {
            return "redirect:/profile";
        }

        if (bindingResult.hasErrors()) {
            return "address/form";
        }

        addressService.createAddress(profile.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm địa chỉ mới thành công");
        return "redirect:/address";
    }

    @GetMapping("/edit/{addressId}")
    public String showEditForm(@PathVariable("addressId") Long addressId,
                               @ModelAttribute("currentProfile") Profile profile,
                               Model model) {
        if (profile == null) {
            return "redirect:/login";
        }
        
        Address address = addressService.getAddressById(addressId);
        if (address == null || !address.getProfileId().equals(profile.getId())) {
            return "redirect:/address";
        }

        if (!model.containsAttribute("addressRequest")) {
            CreateAddressRequest request = CreateAddressRequest.builder()
                    .recipientName(address.getRecipientName())
                    .phone(address.getPhone())
                    .addressLine(address.getAddressLine())
                    .city(address.getCity())
                    .country(address.getCountry())
                    .build();
            model.addAttribute("addressRequest", request);
        }
        
        model.addAttribute("addressId", addressId);
        return "address/form";
    }

    @PostMapping("/edit/{addressId}")
    public String update(@PathVariable("addressId") Long addressId,
                         @ModelAttribute("currentProfile") Profile profile,
                         @Valid @ModelAttribute("addressRequest") CreateAddressRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (profile == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            return "address/form";
        }

        try {
            addressService.updateAddress(addressId, profile.getId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật địa chỉ thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/address";
    }

    @PostMapping("/delete/{addressId}")
    public String delete(@PathVariable("addressId") Long addressId,
                         @ModelAttribute("currentProfile") Profile profile,
                         RedirectAttributes redirectAttributes) {
        if (profile == null) {
            return "redirect:/login";
        }

        try {
            addressService.deleteAddress(addressId, profile.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Xóa địa chỉ thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/address";
    }
}
