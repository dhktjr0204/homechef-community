package com.cooklog.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

		// 사용자가 작성한 모든 게시글 조회 및 UserPostDTO 리스트에 추가
		List<UserPostDTO> boardPosts = boardService.findBoardsByUserId(userId).stream()
			.map(board -> new UserPostDTO(board, null))
			.collect(Collectors.toList());

		// 사용자가 작성한 모든 댓글 조회 및 UserPostDTO 리스트에 추가
		List<UserPostDTO> commentPosts = commentService.findCommentsByUserId(userId).stream()
			.map(comment -> new UserPostDTO(null, comment))
			.collect(Collectors.toList());

		// 게시물과 댓글을 하나의 리스트로 합친다
		posts.addAll(boardPosts);
		posts.addAll(commentPosts);

		// 생성 시간에 따라 정렬
		posts.sort(Comparator.comparing(UserPostDTO::getCreatedAt).reversed());

		return posts;
	}
}
