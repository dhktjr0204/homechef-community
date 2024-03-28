package com.cooklog.service;

import java.util.List;
import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CommentDTO;
import org.springframework.data.domain.Page;

public interface CommentService {
  
	List<CommentDTO> findAllComments();
	void deleteComment(Long commentId);
	// List<CommentDTO> findCommentsByUserId(Long userId);
  
	List<CommentDTO> getCommentInfoByBoardId(Page<BoardDTO> allBoard);
	List<CommentDTO> findCommentsByUserId(Long userId);
}
