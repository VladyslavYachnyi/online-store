package com.myshop.onlinestore.repository;

import com.myshop.onlinestore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUserId(Long userId);

    List<Product> findByPriceBetweenOrderByCreatedAtDesc(BigDecimal min, BigDecimal max);
    List<Product> findByPriceBetweenOrderByCreatedAtAsc(BigDecimal min, BigDecimal max);
    List<Product> findByPriceBetweenOrderByPriceAsc(BigDecimal min, BigDecimal max);
    List<Product> findByPriceBetweenOrderByPriceDesc(BigDecimal min, BigDecimal max);

    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.price BETWEEN :min AND :max " +
            "ORDER BY p.createdAt DESC")
    List<Product> searchByNameOrDescriptionAndPriceBetweenOrderByCreatedAtDesc(
            @Param("keyword") String keyword,
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max);

    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.price BETWEEN :min AND :max " +
            "ORDER BY p.createdAt ASC")
    List<Product> searchByNameOrDescriptionAndPriceBetweenOrderByCreatedAtAsc(
            @Param("keyword") String keyword,
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max);

    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.price BETWEEN :min AND :max " +
            "ORDER BY p.price ASC")
    List<Product> searchByNameOrDescriptionAndPriceBetweenOrderByPriceAsc(
            @Param("keyword") String keyword,
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max);

    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.price BETWEEN :min AND :max " +
            "ORDER BY p.price DESC")
    List<Product> searchByNameOrDescriptionAndPriceBetweenOrderByPriceDesc(
            @Param("keyword") String keyword,
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max);

}
