package com.myshop.onlinestore.service;

import com.myshop.onlinestore.entity.Comment;
import com.myshop.onlinestore.entity.Product;
import com.myshop.onlinestore.entity.User;
import com.myshop.onlinestore.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsByProduct(Product product) {
        return commentRepository.findByProductOrderByCreatedAtDesc(product);
    }

    public void addComment(String text, User user, Product product) {
        Comment comment = new Comment(text, user, product);
        commentRepository.save(comment);
    }
}