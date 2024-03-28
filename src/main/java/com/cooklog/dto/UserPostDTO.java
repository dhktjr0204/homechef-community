package com.cooklog.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPostDTO {
	private String boardContent;
	private LocalDateTime boardCreatedAt;
	private String commentContent;
	private LocalDateTime commentCreatedAt;

	public UserPostDTO(BoardDTO board, CommentDTO comment) {
		if (board != null) {
			this.boardContent = board.getContent();
			this.boardCreatedAt = board.getCreatedAt();
		}
		if (comment != null) {
			this.commentContent = comment.getContent();
			this.commentCreatedAt = comment.getCreatedAt();
		}
	}
}
