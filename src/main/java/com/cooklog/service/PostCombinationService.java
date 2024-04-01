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

		for (BoardDTO board : boards) {
			List<CommentDTO> comments = commentService.findCommentsByBoardId(board.getId());

			for (CommentDTO comment : comments) {
				UserPostDTO post = new UserPostDTO(board, comment);
				posts.add(post);
			}

			// 게시글에 댓글이 없는 경우를 처리
			if (comments.isEmpty()) {
				posts.add(new UserPostDTO(board, null));
			}
		}

		return posts;
	}
}
