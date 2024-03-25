package com.cooklog.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {
	private Long id;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long userId;
	private String userName; // 사용자 이름 또는 닉네임을 표시하기 위함
	private Long boardId; // 댓글이 속한 게시글 ID
}
