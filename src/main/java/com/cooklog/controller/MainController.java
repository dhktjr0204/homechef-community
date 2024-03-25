package com.cooklog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cooklog.dto.UserDTO;
import com.cooklog.service.UserService;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final UserService userService;

	@GetMapping("/")
	public String index(Model model) {
		UserDTO userDto = userService.findUserById(1L);

		model.addAttribute("user", userDto);

		return "index";
	}
}
