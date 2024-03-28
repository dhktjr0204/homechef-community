package com.cooklog.service;

import java.util.List;

import com.cooklog.dto.CommentDTO;

public interface CommentService {
	List<CommentDTO> findAllComments();
	void deleteComment(Long commentId);
	// List<CommentDTO> findCommentsByUserId(Long userId);
}
