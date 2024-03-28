package com.cooklog.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
	private Long id;
	private String content;
	private LocalDateTime createdAt;
	private Long userId;
	private String userName; // 사용자 이름 또는 닉네임을 표시하기 위함
	private Long boardId; // 댓글이 속한 게시글 ID
  private int contentCount;// 댓글 개수

	public CommentDTO(Long id, String content, LocalDateTime createdAt, Long idx, String nickname, Long boardId) {
		this.id = id;
		this.content = content;
		this.createdAt = createdAt;
		this.userId = idx;
		this.userName = nickname;
		this.boardId = boardId;
	}

	public CommentDTO(Long id, String content, LocalDateTime createdAt) {
		this.id = id;
		this.content = content;
		this.createdAt = createdAt;
	}
}
