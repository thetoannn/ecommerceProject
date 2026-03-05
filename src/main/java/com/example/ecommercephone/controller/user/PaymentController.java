package com.example.ecommercephone.controller.user;

import com.example.ecommercephone.dto.request.PaymentRequest;
import com.example.ecommercephone.entity.Payment;
import com.example.ecommercephone.entity.Profile;
import com.example.ecommercephone.enums.PaymentMethod;
import com.example.ecommercephone.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public String list(@ModelAttribute("currentProfile") Profile profile, Model model) {
        if (profile == null) {
            return "redirect:/login";
        }
        List<Payment> payments = paymentService.getPaymentsByProfileId(profile.getId());
        model.addAttribute("payments", payments);
        return "payment/list";
    }

    @GetMapping("/create")
    public String showCreateForm(@ModelAttribute("currentProfile") Profile profile, Model model) {
        if (profile == null) {
            return "redirect:/login";
        }
        if (!model.containsAttribute("paymentRequest")) {
            model.addAttribute("paymentRequest", new PaymentRequest());
        }
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "payment/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("currentProfile") Profile profile,
                         @Valid @ModelAttribute("paymentRequest") PaymentRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (profile == null) {
            return "redirect:/profile";
        }

        if (request.getMethod() != PaymentMethod.COD && 
            (request.getAccountNumber() == null || request.getAccountNumber().isBlank())) {
            bindingResult.rejectValue("accountNumber", "error.paymentRequest", "Số tài khoản/thẻ không được để trống cho phương thức này");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("paymentMethods", PaymentMethod.values());
            return "payment/form";
        }

        paymentService.createPayment(profile.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm phương thức mới thành công");
        return "redirect:/payment";
    }

    @GetMapping("/edit/{paymentId}")
    public String showEditForm(@PathVariable("paymentId") Long paymentId,
                               @ModelAttribute("currentProfile") Profile profile,
                               Model model) {
        if (profile == null) {
            return "redirect:/profile";
        }

        Payment payment = paymentService.getPaymentById(paymentId);
        if (payment == null || !payment.getProfileId().equals(profile.getId())) {
            return "redirect:/payment";
        }

        if (!model.containsAttribute("paymentRequest")) {
            PaymentRequest request = PaymentRequest.builder()
                    .method(payment.getMethod())
                    .accountNumber(payment.getAccountNumber())
                    .isDefault(payment.isDefault())
                    .build();
            model.addAttribute("paymentRequest", request);
        }

        model.addAttribute("paymentId", paymentId);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "payment/form";
    }

    @PostMapping("/edit/{paymentId}")
    public String update(@PathVariable("paymentId") Long paymentId,
                         @ModelAttribute("currentProfile") Profile profile,
                         @Valid @ModelAttribute("paymentRequest") PaymentRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (profile == null) {
            return "redirect:/profile";
        }

        if (request.getMethod() != PaymentMethod.COD && 
            (request.getAccountNumber() == null || request.getAccountNumber().isBlank())) {
            bindingResult.rejectValue("accountNumber", "error.paymentRequest", "Số tài khoản/thẻ không được để trống cho phương thức này");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("paymentId", paymentId);
            return "payment/form";
        }

        try {
            paymentService.updatePayment(paymentId, profile.getId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/payment";
    }

    @PostMapping("/delete/{paymentId}")
    public String delete(@PathVariable("paymentId") Long paymentId,
                         @ModelAttribute("currentProfile") Profile profile,
                         RedirectAttributes redirectAttributes) {
        if (profile == null) {
            return "redirect:/profile";
        }

        try {
            paymentService.deletePayment(paymentId, profile.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Xóa thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/payment";
    }

    @PostMapping("/set-default/{paymentId}")
    public String setDefault(@PathVariable("paymentId") Long paymentId,
                             @ModelAttribute("currentProfile") Profile profile,
                             RedirectAttributes redirectAttributes) {
        if (profile == null) {
            return "redirect:/profile";
        }

        try {
            paymentService.setDefaultPayment(paymentId, profile.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Đã đặt làm mặc định");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/payment";
    }
}
