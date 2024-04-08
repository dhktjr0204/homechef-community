package com.cooklog.service;

import java.util.List;
import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CommentDTO;
import com.cooklog.model.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

	List<CommentDTO> findAllComments();

	List<CommentDTO> getCommentInfoByBoardId(Page<BoardDTO> allBoard);

	List<CommentDTO> findCommentsByUserId(Long userId);

	CommentDTO addComment(Long boardId, CommentDTO commentDTO);

	List<CommentDTO> findCommentsByBoardId(Long id);

	// 댓글 수정 메서드
	CommentDTO updateComment(Long commentId, CommentDTO commentDTO);

	// 댓글 삭제 메서드
	void deleteComment(Long commentId);

	// 게시판 ID에 따른 페이징 처리된 댓글 목록 가져오기
	Page<CommentDTO> getCommentsByBoardId(Long boardId, int page, int limit);


	Page<CommentDTO> searchComments(String category, String term,  Pageable pageable);
}
