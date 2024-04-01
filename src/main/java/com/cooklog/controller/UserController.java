package com.cooklog.controller;

import com.cooklog.dto.JoinDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cooklog.dto.UserDTO;
import com.cooklog.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/profile")
    public String userProfile(Model model) {
        // 예시로 1번 ID 사용자 정보를 조회
        UserDTO userDto = userService.findUserById(1L);

        if (userDto != null) {
            model.addAttribute("user", userDto);
        }
        return "main/index";
    }

    //로그인 뷰
    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    //회원가입 뷰
    @GetMapping("/join")
    public String join() {
        return "user/join";
    }

    //회원가입 폼 처리 시 호출됨
    @PostMapping("/joinProc")
    public String joinProc(@ModelAttribute JoinDTO joinDTO) {
        userService.joinSave(joinDTO);
        return "redirect:/login";
    }

}
