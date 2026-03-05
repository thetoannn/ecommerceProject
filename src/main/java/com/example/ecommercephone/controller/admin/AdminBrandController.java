package com.example.ecommercephone.controller.admin;

import com.example.ecommercephone.dto.request.AdminBrandRequest;
import com.example.ecommercephone.entity.Brand;
import com.example.ecommercephone.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/brands")
public class AdminBrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String query,
                       @RequestParam(value = "page", defaultValue = "1") int currentPage,
                       @RequestParam(value = "size", defaultValue = "10") int pageSize,
                       Model model) {
        Page<Brand> brandPage = brandService.searchBrands(query, currentPage, pageSize);

        int totalPages = brandPage.getTotalPages();
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + 4);
        startPage = Math.max(1, endPage - 4);

        model.addAttribute("brands", brandPage.getContent());
        model.addAttribute("query", query);
        model.addAttribute("totalBrands", brandService.countAll());
        model.addAttribute("totalItems", brandPage.getTotalElements());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("brandForm", new AdminBrandRequest());
        return "admin/brands/list";
    }

    @GetMapping("/new")
    public String newBrand(Model model) {
        if (!model.containsAttribute("brandForm")) {
            model.addAttribute("brandForm", new AdminBrandRequest());
        }
        return "admin/brands/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("brandForm") AdminBrandRequest form,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (!bindingResult.hasFieldErrors("name") && brandService.existsByName(form.getName())) {
            bindingResult.addError(new FieldError("brandForm", "name", "Tên hãng đã tồn tại"));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("brandForm", form);
            return "admin/brands/form";
        }

        brandService.createBrand(form);

        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm hãng điện thoại mới thành công");
        return "redirect:/admin/brands";
    }
}

