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

    //로그인
    @GetMapping("/login")
    public String loginP() {
        return "user/login";
    }

    //회원가입
    @GetMapping("/join")
    public String joinP() {
        return "user/join";
    }
    @PostMapping("/joinProc")
    public String joinProcess(JoinDTO joinDTO) {
        userService.join(joinDTO);
        return "redirect:/login";
    }

    @GetMapping("/")
    public String mainP() {
        return "user/main";
    }


}
