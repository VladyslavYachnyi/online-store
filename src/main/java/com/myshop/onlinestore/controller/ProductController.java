package com.myshop.onlinestore.controller;

import com.myshop.onlinestore.entity.Product;
import com.myshop.onlinestore.entity.Role;
import com.myshop.onlinestore.entity.User;
import com.myshop.onlinestore.repository.ProductRepository;
import com.myshop.onlinestore.repository.UserRepository;
import com.myshop.onlinestore.service.ProductService;
import com.myshop.onlinestore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;
    private ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProductController(ProductService productService, UserService userService, ProductRepository productRepository,  UserRepository userRepository) {
        this.productService = productService;
        this.userService = userService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listProducts(
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "sort", required = false, defaultValue = "newest") String sort,
            @RequestParam(name = "name", required = false) String name,
            Model model,
            Principal principal) {

        List<Product> products = productService.findFilteredSortedAndSearched(minPrice, maxPrice, sort, name);

        model.addAttribute("products", products);
        model.addAttribute("sort", sort);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("name", name);

        if (principal != null) {
            model.addAttribute("currentUserEmail", principal.getName());
        }

        return "product-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, Principal principal) {
        model.addAttribute("product", new Product());

        if (principal != null) {
            model.addAttribute("currentUserEmail", principal.getName());
        }

        return "product-form";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("image") MultipartFile imageFile,
                             Principal principal) throws IOException {

        if (principal == null) {
            return "redirect:/login";
        }

        userService.getUserByEmail(principal.getName())
                .ifPresent(product::setUser);

        Path uploadPath = Paths.get("uploads").toAbsolutePath();
        Files.createDirectories(uploadPath);

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            imageFile.transferTo(filePath);
            product.setImagePath("/uploads/" + fileName);
        }

        productService.saveProduct(product);
        return "redirect:/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, Principal principal) {
        Optional<Product> productOpt = productService.getProductById(id);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            Optional<User> currentUserOpt = userService.getUserByEmail(principal.getName());
            if (currentUserOpt.isPresent()) {
                User currentUser = currentUserOpt.get();

                if (product.getUser().getEmail().equals(currentUser.getEmail()) ||
                        currentUser.getRole() == Role.ROLE_ADMIN) {

                    if (product.getImagePath() != null) {
                        Path imagePath = Paths.get("uploads", Paths.get(product.getImagePath()).getFileName().toString());
                        try {
                            Files.deleteIfExists(imagePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    productService.deleteProductById(id);

                    if (currentUser.getRole() == Role.ROLE_ADMIN) {
                        return "redirect:/products";
                    }
                }
            }
        }

        return "redirect:/products/my";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));

        if (!product.getUser().getEmail().equals(principal.getName())) {
            return "redirect:/products";
        }

        model.addAttribute("product", product);
        if (principal != null) {
            model.addAttribute("currentUserEmail", principal.getName());
        }
        return "product-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute Product updatedProduct,
                                @RequestParam(value = "image", required = false) MultipartFile imageFile,
                                Principal principal) throws IOException {
        Product existingProduct = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));

        if (!existingProduct.getUser().getEmail().equals(principal.getName())) {
            return "redirect:/products";
        }

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());

        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path imagePath = Paths.get("uploads", filename);
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, imageFile.getBytes());

            existingProduct.setImagePath("/uploads/" + filename);
        }

        productService.saveProduct(existingProduct);
        return "redirect:/products/my";
    }

    @GetMapping("/{id}")
    public String viewProductDetails(@PathVariable Long id, Model model, Principal principal) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
        model.addAttribute("product", product);

        if (principal != null) {
            model.addAttribute("currentUserEmail", principal.getName());
        }

        return "product-detail";
    }

    @GetMapping("/my")
    public String viewMyProducts(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.getUserByEmail(principal.getName());
        if (userOpt.isPresent()) {
            List<Product> userProducts = productService.getProductsByUserId(userOpt.get().getId());
            model.addAttribute("products", userProducts);
            model.addAttribute("currentUserEmail", principal.getName());
            return "product-my";
        }

        return "redirect:/products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        return "product-detail";
    }

    @GetMapping("/seller/{userId}")
    public String getProductsBySeller(@PathVariable Long userId, Model model, Principal principal) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        List<Product> sellerProducts = productRepository.findByUserId(userId);

        model.addAttribute("products", sellerProducts);
        model.addAttribute("username", seller.getUsername());

        if (principal != null) {
            model.addAttribute("currentUserEmail", principal.getName());
        }

        return "product-seller";
    }

}
