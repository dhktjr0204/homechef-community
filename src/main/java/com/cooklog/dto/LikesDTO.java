package com.cooklog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikesDTO {
	private Long id;
	private Long userId;
	private Long boardId;
	private boolean liked; // 사용자가 '좋아요'를 눌렀는지 여부
}