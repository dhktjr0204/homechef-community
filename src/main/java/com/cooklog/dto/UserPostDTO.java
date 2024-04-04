package com.cooklog.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPostDTO {
	private Long id; // 게시물 또는 댓글의 ID
	private String content; // 게시물 또는 댓글의 내용
	private LocalDateTime createdAt; // 생성 시간
	private boolean isBoard; // 게시물이면 true, 댓글이면 false

	// BoardDTO와 CommentDTO를 받아서 UserPostDTO를 생성
	public UserPostDTO(BoardDTO board, CommentDTO comment) {
		if (board != null) {
			this.id = board.getId();
			this.content = board.getContent();
			this.createdAt = board.getCreatedAt();
			this.isBoard = true;
		} else if (comment != null) {
			this.id = comment.getId();
			this.content = comment.getContent();
			this.createdAt = comment.getCreatedAt();
			this.isBoard = false;
		}
	}
}
