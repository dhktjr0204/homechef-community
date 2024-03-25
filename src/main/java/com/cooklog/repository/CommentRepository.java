package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
