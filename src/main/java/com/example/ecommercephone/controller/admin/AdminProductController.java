package com.example.ecommercephone.controller.admin;

import com.example.ecommercephone.dto.request.AdminProductRequest;
import com.example.ecommercephone.dto.response.AdminProductResponse;
import com.example.ecommercephone.dto.response.ProductStatistics;
import com.example.ecommercephone.entity.Product;
import com.example.ecommercephone.entity.AttributeValue;
import com.example.ecommercephone.service.AttributeService;
import com.example.ecommercephone.service.BrandService;
import com.example.ecommercephone.service.ProductImageService;
import com.example.ecommercephone.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private BrandService brandService;

    @GetMapping
    public String listProducts(@RequestParam(value = "page", defaultValue = "1") int currentPage,
                              @RequestParam(value = "size", defaultValue = "10") int pageSize,
                              Model model) {
        Page<Product> productPage = productService.getProductsAdmin(currentPage, pageSize);
        ProductStatistics statistics = productService.getProductStatistics();

        addFormAttributes(model);
        addPaginationAttributes(model, productPage, currentPage, pageSize);
        addStatisticsAttributes(model, statistics);
        addFormData(model);
        return "admin/products/list";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam(value = "q", required = true) String query,
                                @RequestParam(value = "page", defaultValue = "1") int currentPage,
                                @RequestParam(value = "size", defaultValue = "10") int pageSize,
                                Model model) {
        if (!StringUtils.hasText(query)) {
            return "redirect:/admin/products?page=" + currentPage + "&size=" + pageSize;
        }

        Page<Product> productPage = productService.searchProductsAdmin(query, currentPage, pageSize);
        ProductStatistics statistics = productService.getProductStatistics();

        addFormAttributes(model);
        addPaginationAttributes(model, productPage, currentPage, pageSize);
        addStatisticsAttributes(model, statistics);
        addFormData(model);

        model.addAttribute("query", query);
        return "admin/products/list";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("productForm") AdminProductRequest request,
                         BindingResult bindingResult,
                         @RequestParam(value = "images", required = false) MultipartFile[] images,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("productForm", request);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productForm", bindingResult);
            redirectAttributes.addFlashAttribute("productFormMode", "create");
            redirectAttributes.addFlashAttribute("productModalActive", true);
            return "redirect:/admin/products";
        }

        try {
            productService.createProduct(request, images);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sản phẩm mới thành công");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("productForm", request);
            redirectAttributes.addFlashAttribute("productFormMode", "create");
            redirectAttributes.addFlashAttribute("productModalActive", true);
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/update")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("productForm") AdminProductRequest request,
                                BindingResult bindingResult,
                                @RequestParam(value = "images", required = false) MultipartFile[] images,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            request.setId(id);
            redirectAttributes.addFlashAttribute("productForm", request);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productForm", bindingResult);
            redirectAttributes.addFlashAttribute("productFormMode", "edit");
            redirectAttributes.addFlashAttribute("productModalActive", true);
            redirectAttributes.addFlashAttribute("productEditingId", id);
            return "redirect:/admin/products";
        }

        try {
            productService.updateProduct(id, request, images);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật sản phẩm thành công");
            redirectAttributes.addFlashAttribute("refreshImages", true);
            redirectAttributes.addFlashAttribute("updatedProductId", id);
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            request.setId(id);
            redirectAttributes.addFlashAttribute("productForm", request);
            redirectAttributes.addFlashAttribute("productFormMode", "edit");
            redirectAttributes.addFlashAttribute("productModalActive", true);
            redirectAttributes.addFlashAttribute("productEditingId", id);
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/json")
    @ResponseBody
    public ResponseEntity<AdminProductResponse> getProduct(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(AdminProductResponse.from(productService.findById(id)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/images/{imageId}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        if (imageId == null) {
            return ResponseEntity.badRequest().body("Thiếu ID ảnh");
        }
        try {
            productImageService.deleteProductImage(imageId);
            return ResponseEntity.ok("Đã xóa ảnh thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa ảnh: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/delete")
    public String softDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.softDeleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/restore")
    public String restore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.restoreProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã khôi phục sản phẩm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi khôi phục sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes) {
        try {
            productService.changeProductStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái sản phẩm");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    private void addFormAttributes(Model model) {
        if (!model.containsAttribute("productForm")) {
            model.addAttribute("productForm", new AdminProductRequest());
        }
        if (!model.containsAttribute("productFormMode")) {
            model.addAttribute("productFormMode", "create");
        }
        if (!model.containsAttribute("productModalActive")) {
            model.addAttribute("productModalActive", false);
        }
        if (!model.containsAttribute("productEditingId")) {
            model.addAttribute("productEditingId", null);
        }
    }

    private void addPaginationAttributes(Model model, Page<Product> productPage, int currentPage, int pageSize) {
        int totalPages = productPage.getTotalPages();
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + 4);
        startPage = Math.max(1, endPage - 4);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
    }

    private void addStatisticsAttributes(Model model, ProductStatistics statistics) {
        model.addAttribute("totalProducts", statistics.getTotalProducts());
        model.addAttribute("outOfStock", statistics.getOutOfStock());
        model.addAttribute("lowStock", statistics.getLowStock());
        model.addAttribute("inventoryValue", statistics.getInventoryValue());
    }

    private void addFormData(Model model) {
        List<Map<String, Object>> brandsForJs = brandService.findAllBrands(Sort.by("name")).stream()
            .map(brand -> {
                Map<String, Object> brandMap = new HashMap<>();
                brandMap.put("id", brand.getId());
                brandMap.put("name", brand.getName());
                return brandMap;
            })
            .toList();

        List<Map<String, Object>> attributesForJs = attributeService.findAllAttributes().stream()
            .map(attr -> {
                Map<String, Object> attributeMap = new HashMap<>();
                attributeMap.put("id", attr.getId());
                attributeMap.put("name", attr.getName());
                List<Map<String, Object>> values = (attr.getValues() != null ? attr.getValues() : Collections.<AttributeValue>emptyList())
                    .stream()
                    .map(val -> {
                        Map<String, Object> valMap = new HashMap<>();
                        valMap.put("id", val.getId());
                        valMap.put("value", val.getValue());
                        return valMap;
                    })
                    .toList();
                attributeMap.put("values", values);
                return attributeMap;
            })
            .toList();

        model.addAttribute("brands", brandsForJs);
        model.addAttribute("attributes", attributeService.findAllAttributes());
        model.addAttribute("attributesForJs", attributesForJs);
    }
}
