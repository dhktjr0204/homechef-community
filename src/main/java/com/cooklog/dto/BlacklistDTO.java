package com.cooklog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlacklistDTO {
	private Long id;
	private Long userId;
	private String createdAt;
}
