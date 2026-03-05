package com.example.ecommercephone.controller.user;

import com.example.ecommercephone.entity.CartItem;
import com.example.ecommercephone.entity.Order;
import com.example.ecommercephone.enums.PaymentMethod;
import com.example.ecommercephone.service.AddressService;
import com.example.ecommercephone.service.CartService;
import com.example.ecommercephone.service.CheckoutService;
import com.example.ecommercephone.service.PaymentService;
import com.example.ecommercephone.repository.AccountRepository;
import com.example.ecommercephone.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.ecommercephone.dto.request.CheckoutRequest;
import com.example.ecommercephone.repository.ProfileRepository;
import com.example.ecommercephone.entity.Profile;
import com.example.ecommercephone.entity.Address;
import com.example.ecommercephone.entity.Payment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckOutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProfileRepository profileRepository;


    @GetMapping
    public String checkoutPage(@AuthenticationPrincipal UserDetails principal,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thanh toán");
            return "redirect:/login";
        }

        Account account = accountRepository.findByUsername(principal.getUsername()).orElse(null);
        if (account == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.getCartItems(account.getUid());
        if (cartItems == null || cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng trống");
            return "redirect:/cart";
        }

        BigDecimal total = cartService.calculateTotal(cartItems);
        
        Profile profile = profileRepository.findByAccountUid(account.getUid()).orElse(null);
        if (profile != null) {
            List<Address> addresses = addressService.getAddressesByProfileId(profile.getId());
            List<Payment> payments = paymentService.getPaymentsByProfileId(profile.getId());
            model.addAttribute("addresses", addresses);
            model.addAttribute("payments", payments);
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", total);
        model.addAttribute("checkoutRequest", new CheckoutRequest());
        return "checkout/view";
    }

    @PostMapping
    public String checkout(@AuthenticationPrincipal UserDetails principal,
                           @ModelAttribute CheckoutRequest checkoutRequest,
                           RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thanh toán");
            return "redirect:/login";
        }

        try {
            Order order = checkoutService.processCheckout(principal, 
                    checkoutRequest.getAddressId(), 
                    checkoutRequest.getPaymentId(), 
                    checkoutRequest.getNotes());
            redirectAttributes.addFlashAttribute("successMessage", "Đặt hàng thành công!");
            return "redirect:/orders/" + order.getId();
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/checkout";
        }
    }
}
