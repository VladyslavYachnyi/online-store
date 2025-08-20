package com.myshop.onlinestore.service;

import com.myshop.onlinestore.entity.Product;
import com.myshop.onlinestore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getProductsByUserId(Long userId) {
        return productRepository.findByUserId(userId);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }


    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }


    public List<Product> findFilteredSortedAndSearched(BigDecimal min, BigDecimal max, String sort, String keyword) {
        if (min == null) min = BigDecimal.ZERO;
        if (max == null) max = BigDecimal.valueOf(Double.MAX_VALUE);
        if (keyword == null || keyword.trim().isEmpty()) keyword = "";

        return switch (sort) {
            case "oldest"    -> productRepository.searchByNameOrDescriptionAndPriceBetweenOrderByCreatedAtAsc(keyword, min, max);
            case "priceAsc"  -> productRepository.searchByNameOrDescriptionAndPriceBetweenOrderByPriceAsc(keyword, min, max);
            case "priceDesc" -> productRepository.searchByNameOrDescriptionAndPriceBetweenOrderByPriceDesc(keyword, min, max);
            default          -> productRepository.searchByNameOrDescriptionAndPriceBetweenOrderByCreatedAtDesc(keyword, min, max);
        };
    }

    public List<Product> getRecentProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.findAll(pageable).getContent();
    }


}
