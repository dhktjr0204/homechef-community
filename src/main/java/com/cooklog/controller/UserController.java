package com.cooklog.controller;

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


//    @PostMapping("/signin/signup")
//    @ResponseBody
//    public ResponseEntity<String> signup(@RequestBody @Valid UserDTO userDTO) {
//        userService.join(userDTO);
//        return ResponseEntity.ok("OK");
//    }

    @GetMapping("/signin/signup")
    public String signUpForm(@ModelAttribute("userDTO") UserDTO userDTO){
        return "user/signup";
    }


    @PostMapping("/signin/signup")
    public String signUp(@ModelAttribute("userDTO") UserDTO userDTO){
        userService.join(userDTO);
        return "user/signin";
    }


    @GetMapping("/signin")
    public String signInForm(@ModelAttribute("userDTO") UserDTO userDTO){
        return "user/signin";
    }

//    @PostMapping("/signIn")
//    public String signIn(){
//
//    }


}
