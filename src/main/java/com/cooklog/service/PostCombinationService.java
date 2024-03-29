package com.cooklog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CommentDTO;
import com.cooklog.dto.UserPostDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostCombinationService {
	private final BoardService boardService;
	private final CommentService commentService;

	public List<UserPostDTO> combineBoardsAndComments(Long userId) {
		List<UserPostDTO> posts = new ArrayList<>();

		List<BoardDTO> boards = boardService.findBoardsByUserId(userId);
		List<CommentDTO> comments = commentService.findCommentsByUserId(userId);

		for (int i = 0; i < boards.size(); i++) {
			BoardDTO board = boards.get(i);
			CommentDTO comment = comments.size() > i ? comments.get(i) : null; // 댓글이 없을 수도 있으므로 체크

			UserPostDTO post = new UserPostDTO(board, comment);
			posts.add(post);
		}

		return posts;
	}
}
