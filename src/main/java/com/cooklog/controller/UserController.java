package com.cooklog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cooklog.dto.UserDTO;
import com.cooklog.service.UserService;

@Controller
public class UserController {

	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/user/profile")
	public String userProfile(Model model) {
		// 예시로 1번 ID 사용자 정보를 조회
		UserDTO userDto = userService.findUserById(1L);

		if (userDto != null) {
			model.addAttribute("user", userDto);
		}
		return "index";
	}
}
