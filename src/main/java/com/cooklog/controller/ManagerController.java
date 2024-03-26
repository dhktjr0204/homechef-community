package com.cooklog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cooklog.dto.UserDTO;
import com.cooklog.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerController {

	private final UserService userService;

	@GetMapping("/main")
	public String userProfile(Model model) {
		// 예시로 1번 ID 사용자 정보를 조회
		UserDTO userDto = userService.findUserById(1L);

		if (userDto != null) {
			model.addAttribute("user", userDto);
		}
		return "manager/manager";
	}

	@GetMapping("/board")
	public String board() {
		return "manager/board-manager";
	}

	@GetMapping("/comment")
	public String command() {
		return "manager/comment-manager";
	}

	@GetMapping("/report")
	public String report() {
		return "manager/report-manager";
	}
}
