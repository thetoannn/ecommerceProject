package com.example.ecommercephone.controller.admin;

import com.example.ecommercephone.dto.request.ProductAttributeRequest;
import com.example.ecommercephone.entity.Product;
import com.example.ecommercephone.service.ProductAttributeService;
import com.example.ecommercephone.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products/{productId}/attributes")
public class ProductAttributeController {

    @Autowired
    private ProductAttributeService productAttributeService;

    @Autowired
    private ProductService productService;

    @PostMapping("/update")
    public String updateProductAttributes(@PathVariable Long productId,
                                          @Valid @ModelAttribute ProductAttributeRequest attributeRequest,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                bindingResult.getFieldError().getDefaultMessage());
            return "redirect:/admin/products/" + productId + "/edit";
        }

        Product product;
        try {
            product = productService.findById(productId);
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/products";
        }

        try {
            productAttributeService.updateProductAttributes(product, attributeRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thuộc tính sản phẩm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi khi lưu thuộc tính sản phẩm: " + e.getMessage());
            return "redirect:/admin/products/" + productId + "/edit";
        }

        return "redirect:/admin/products";
    }
}
