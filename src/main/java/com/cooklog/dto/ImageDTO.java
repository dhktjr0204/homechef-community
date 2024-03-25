package com.cooklog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDTO {
	private Long id;
	private Long boardId;
	private String name;
	private Integer order;

}
