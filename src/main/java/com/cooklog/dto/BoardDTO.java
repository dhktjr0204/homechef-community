package com.cooklog.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDTO {

	private Long id;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Integer readCount;
	private Long userId; // User의 식별자
	private String userNickname; // User의 닉네임
	private List<String> imageUrls; // Image의 URL 목록
	private List<String> tags; // 태그 이름 목록
	private Integer likeCount; // '좋아요' 개수
}
