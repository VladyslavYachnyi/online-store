package com.myshop.onlinestore.controller;

import com.myshop.onlinestore.entity.Product;
import com.myshop.onlinestore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;

    @Autowired
    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
        List<Product> recentProducts = productService.getRecentProducts(6);
        model.addAttribute("recentProducts", recentProducts);
        return "index";
    }
}
