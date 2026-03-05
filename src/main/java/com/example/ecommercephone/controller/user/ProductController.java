package com.example.ecommercephone.controller.user;

import com.example.ecommercephone.entity.Product;
import com.example.ecommercephone.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    private static final int DEFAULT_PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size,
                       Model model) {
        if (size < 1) size = DEFAULT_PAGE_SIZE;
        if (size > 100) size = 100;
        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.search(q, pageable);

        if (productPage.getTotalPages() > 0 && page >= productPage.getTotalPages()) {
            int lastPage = productPage.getTotalPages() - 1;
            StringBuilder redirectUrl = new StringBuilder("/products?page=").append(lastPage);
            if (q != null && !q.trim().isEmpty()) {
                redirectUrl.append("&q=").append(q);
            }
            return "redirect:" + redirectUrl;
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("productPage", productPage);
        model.addAttribute("q", q);

        return "product/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product;
        try {
            product = productService.findById(id);
        } catch (EntityNotFoundException e) {
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        model.addAttribute("attributes", productService.getProductAttributeMap(product));
        model.addAttribute("colorOptions", productService.getColorOptions(product));
        model.addAttribute("productImages", productService.getProductImages(product));
        
        Map<String, String> colorMap = Map.of(
            "Natural Titanium", "#8E8E93",
            "Black", "#000000",
            "Blue", "#007AFF",
            "White", "#FFFFFF",
            "Red", "#FF3B30",
            "Green", "#34C759",
            "Yellow", "#FFCC00",
            "Purple", "#AF52DE"
        );
        model.addAttribute("colorMap", colorMap);
        
        return "product/detail";
    }
}


