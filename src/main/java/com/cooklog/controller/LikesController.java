package com.cooklog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooklog.service.LikesService;

@RestController
@RequestMapping("/api/likes")
public class LikesController {

	private final LikesService likesService;

	@Autowired
	public LikesController(LikesService likesService) {
		this.likesService = likesService;
	}

	// 기타 엔드포인트...
}
