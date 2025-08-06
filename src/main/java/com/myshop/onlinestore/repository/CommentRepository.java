package com.myshop.onlinestore.repository;

import com.myshop.onlinestore.entity.Comment;
import com.myshop.onlinestore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductOrderByCreatedAtDesc(Product product);
}