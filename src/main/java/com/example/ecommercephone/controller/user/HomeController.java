package com.example.ecommercephone.controller.user;

import com.example.ecommercephone.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String index(Model model) {
        var arrivals = productService.findLatest(5);
        var bestSellers = productService.findTopSelling(4);
        model.addAttribute("arrivals", arrivals);
        model.addAttribute("bestSellers", bestSellers);
        return "home";
    }
}
