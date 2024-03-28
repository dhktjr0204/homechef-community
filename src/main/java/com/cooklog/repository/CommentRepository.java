package com.cooklog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.Board;
import com.cooklog.model.Comment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByUserIdx(Long userId);
}
