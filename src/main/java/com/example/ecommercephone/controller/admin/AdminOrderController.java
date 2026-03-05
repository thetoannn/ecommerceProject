package com.example.ecommercephone.controller.admin;

import com.example.ecommercephone.config.CustomUserDetail;
import com.example.ecommercephone.dto.response.*;
import com.example.ecommercephone.enums.OrderStatus;
import com.example.ecommercephone.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String query,
                       @RequestParam(value = "page", defaultValue = "1") int currentPage,
                       @RequestParam(value = "size", defaultValue = "10") int pageSize,
                       Model model) {
        Page<OrderListResponse> orderPage = orderService.searchOrders(query, currentPage, pageSize);

        int totalPages = orderPage.getTotalPages();
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + 4);
        startPage = Math.max(1, endPage - 4);

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("query", query);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalItems", orderPage.getTotalElements());
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        model.addAttribute("totalOrders", orderService.countTotalOrders());
        model.addAttribute("totalRevenue", orderService.getTotalRevenue());
        model.addAttribute("processing", orderService.countByStatus(OrderStatus.PROCESSING));
        model.addAttribute("shipped", orderService.countByStatus(OrderStatus.SHIPPED));
        model.addAttribute("delivered", orderService.countByStatus(OrderStatus.DELIVERED));
        model.addAttribute("cancelled", orderService.countByStatus(OrderStatus.CANCELLED));

        return "admin/orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        try {
            OrderDetailResponse orderDetail = orderService.getOrderDetail(id);

            model.addAttribute("order", orderDetail.getOrder());
            model.addAttribute("orderItems", orderDetail.getOrderItems());
            model.addAttribute("history", orderDetail.getHistory());
            model.addAttribute("totalItems", orderDetail.getTotalItems());

            return "admin/orders/detail";
        } catch (EntityNotFoundException e) {
            return "redirect:/admin/orders";
        }
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               @RequestParam(required = false) String note,
                               @AuthenticationPrincipal CustomUserDetail principal,
                               RedirectAttributes redirectAttributes) {
        try {
            String changedByUid = (principal != null) ? principal.getUid() : null;

            orderService.updateOrderStatus(id, status, changedByUid, note);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/orders/" + id;
    }
}
