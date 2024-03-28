package com.cooklog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimplifiedPostDTO {
	private Long id;
	private String content;

	// 생성자, 게터 및 세터
	public SimplifiedPostDTO(Long id, String content) {
		this.id = id;
		this.content = content;
	}
}
