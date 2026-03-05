package com.example.ecommercephone.controller.admin;

import com.example.ecommercephone.entity.Account;
import com.example.ecommercephone.entity.AccountStatusHistory;
import com.example.ecommercephone.entity.Profile;
import com.example.ecommercephone.enums.AccountStatus;
import com.example.ecommercephone.service.AccountService;
import com.example.ecommercephone.service.AccountStatusHistoryService;
import com.example.ecommercephone.service.ProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AccountStatusHistoryService  accountStatusHistoryService;

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String query,
                       @RequestParam(value = "role", required = false) String role,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "page", defaultValue = "1") int currentPage,
                       @RequestParam(value = "size", defaultValue = "10") int pageSize,
                       Model model) {

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<Account> pageResult = accountService.searchAccounts(query, role, status, pageable);

        int totalPages = pageResult.getTotalPages();
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + 4);
        startPage = Math.max(1, endPage - 4);

        long totalUsers = accountService.countTotalUsers();
        long activeUsers = accountService.countActiveUsers();
        long blockedUsers = accountService.countBlockedUsers();
        long adminUsers = accountService.countAdminUsers();

        model.addAttribute("users", pageResult.getContent());
        model.addAttribute("pageResult", pageResult);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("query", query);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedStatus", status);

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("blockedUsers", blockedUsers);
        model.addAttribute("adminUsers", adminUsers);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/users/list";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Account account = accountService.toggleStatus(id);
            String action = account.getStatus() == AccountStatus.BLOCKED ? "khóa" : "mở khóa";
            redirectAttributes.addFlashAttribute("successMessage", "Đã " + action + " tài khoản " + account.getUsername() + " thành công");
        }
        catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tài khoản");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/accounts/{id}/toggle")
    public String toggleAccount(
            @PathVariable Long id,
            Model model) {

        if (accountService.hasOrder(id)) {
            model.addAttribute("accountId", id);
            model.addAttribute(
                    "message",
                    "Tài khoản hiện có đơn hàng, bạn có chắc chắn muốn khóa?"
            );
            return "account/confirm-lock";
        }

        accountService.toggleStatus(id);
        return "redirect:/accounts";
    }

    @PostMapping("/accounts/{id}/confirm-lock")
    public String confirmLock(@PathVariable Long id) {
        accountService.toggleStatus(id);
        return "redirect:/accounts";
    }


    @GetMapping("/{uid}")
    public String detail(@PathVariable String uid, Model model) {
        try {
            Account accountUser = accountService.findByUid(uid);
            Profile profileUser = profileService.findByAccountUid(uid);
            model.addAttribute("accountUser", accountUser);
            model.addAttribute("profileUser", profileUser);
            return "admin/users/detail";
        } catch (EntityNotFoundException e) {
            return "redirect:/admin/users";
        }
    }

    @GetMapping("/{uid}/history")
    public String history(@PathVariable String uid, Model model) {
        try {
            Account account = accountService.findByUid(uid);
            List<AccountStatusHistory> history = accountStatusHistoryService.findByUidOrderByChangedAtDesc(uid);
            model.addAttribute("account", account);
            model.addAttribute("history", history);
            return "admin/users/history";
        } catch (EntityNotFoundException e) {
            return "redirect:/admin/users";
        }
    }
}

