package com.cooklog.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlacklistDTO {
	private Long id;
	private Long userId;
	private LocalDateTime createdAt;
}
