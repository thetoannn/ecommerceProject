package com.example.ecommercephone.controller.user;

import com.example.ecommercephone.dto.response.CartResponse;
import com.example.ecommercephone.dto.response.RestApiResponse;
import com.example.ecommercephone.entity.CartItem;
import com.example.ecommercephone.service.CartService;
import com.example.ecommercephone.util.AdminAuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping
public class CartController {

    @Autowired
    private CartService cartService;


    @GetMapping("/cart")
    public String viewCart(Model model) {
        String uid = AdminAuthHelper.getCurrentUserUid();
        if (uid == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.getCartItems(uid);
        BigDecimal total = cartService.calculateTotal(cartItems);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", total);
        return "cart/view";
    }

    @GetMapping("/api/cart")
    @ResponseBody
    public ResponseEntity<RestApiResponse<CartResponse>> getCartApi() {
        String uid = AdminAuthHelper.getCurrentUserUid();
        if (uid == null) {
            return ResponseEntity.status(401).body(RestApiResponse.error("Unauthorized", "/login"));
        }
        CartResponse cartResponse = cartService.getCartResponse(uid);
        return ResponseEntity.ok(RestApiResponse.success(cartResponse));
    }

    @GetMapping("/cart/add/{id}")
    public String addToCartWeb(@PathVariable Long id, RedirectAttributes ra) {
        String uid = AdminAuthHelper.getCurrentUserUid();
        if (uid == null) {
            ra.addFlashAttribute("errorMessage", "Vui lòng đăng nhập");
            return "redirect:/login";
        }
        try {
            cartService.addToCart(uid, id, 1);
            ra.addFlashAttribute("cartMessage", "Đã thêm vào giỏ hàng");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/add/{productId}")
    public String addToCartPostWeb(@PathVariable Long productId, @RequestParam(defaultValue = "1") int qty, RedirectAttributes ra) {
        String uid = AdminAuthHelper.getCurrentUserUid();
        if (uid == null) {
            ra.addFlashAttribute("errorMessage", "Vui lòng đăng nhập");
            return "redirect:/login";
        }
        try {
            cartService.addToCart(uid, productId, qty);
            ra.addFlashAttribute("successMessage", "Đã thêm vào giỏ hàng");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateQuantityWeb(@PathVariable Long id, @RequestParam("qty") int qty, RedirectAttributes ra) {
        String uid = AdminAuthHelper.getCurrentUserUid();
        if (uid == null) return "redirect:/login";
        try {
            cartService.updateQuantity(uid, id, qty);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String remove(@PathVariable Long id) {
        String uid = AdminAuthHelper.getCurrentUserUid();
        if (uid != null) {
            cartService.removeFromCart(uid, id);
        }
        return "redirect:/cart";
    }

    @PostMapping("/api/cart/add/{productId}")
    @ResponseBody
    public ResponseEntity<RestApiResponse<CartResponse>> addToCartApi(@PathVariable Long productId, @RequestParam(defaultValue = "1") int qty) {
        String uid = AdminAuthHelper.getCurrentUserUid();
        if (uid == null) {
            return ResponseEntity.status(401).body(RestApiResponse.error("Vui lòng đăng nhập", "/login"));
        }
        try {
            cartService.addToCart(uid, productId, qty);
            CartResponse updatedCart = cartService.getCartResponse(uid);
            return ResponseEntity.ok(RestApiResponse.success("Sản phẩm đã được thêm vào giỏ hàng", updatedCart));
        } catch (Exception e) {
            return ResponseEntity.ok(RestApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/api/cart/update/{id}")
    @ResponseBody
    public ResponseEntity<RestApiResponse<CartResponse>> updateQuantityApi(@PathVariable Long id, @RequestParam("qty") int qty) {
        String uid = AdminAuthHelper.getCurrentUserUid();
        if (uid == null) return ResponseEntity.status(401).body(RestApiResponse.error("Unauthorized"));
        try {
            cartService.updateQuantity(uid, id, qty);
            CartResponse updatedCart = cartService.getCartResponse(uid);
            return ResponseEntity.ok(RestApiResponse.success(updatedCart));
        } catch (Exception e) {
            return ResponseEntity.ok(RestApiResponse.error(e.getMessage()));
        }
    }
}
