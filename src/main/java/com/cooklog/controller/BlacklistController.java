package com.cooklog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooklog.service.BlacklistService;

@RestController
@RequestMapping("/api/blacklist")
public class BlacklistController {

	private final BlacklistService blacklistService;

	@Autowired
	public BlacklistController(BlacklistService blacklistService) {
		this.blacklistService = blacklistService;
	}
	// 기타 엔드포인트...
}
