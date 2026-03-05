package com.example.ecommercephone.controller.user;

import com.example.ecommercephone.config.CustomUserDetail;
import com.example.ecommercephone.entity.Order;
import com.example.ecommercephone.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String myOrders(@AuthenticationPrincipal CustomUserDetail principal, Model model) {
        model.addAttribute("orders", orderService.getOrdersByUid(principal.getUid()));
        return "orders/my-orders";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable("id") Long id,
                              @AuthenticationPrincipal CustomUserDetail principal,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        try {
            Order order = orderService.getOrderDetailForUid(id, principal.getUid());

            model.addAttribute("order", order);
            return "order/detail";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Đơn hàng không tồn tại"
            );
            return "redirect:/orders";
        }
    }
}


