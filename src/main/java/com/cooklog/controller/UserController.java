package com.cooklog.controller;

import com.cooklog.dto.*;
import com.cooklog.model.Role;
import com.cooklog.model.User;
import com.cooklog.validate.JoinValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.cooklog.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    //로그인 유효성 검사
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false)String error,
                        @RequestParam(value = "exception", required = false)String exception, Model model) {

        model.addAttribute("error", error);
        model.addAttribute("exception", exception);

        return "user/login";
    }

    //로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }

    //회원가입 뷰
    @GetMapping("/join")
    public String join() {
        return "user/join";
    }

    //회원가입 폼 처리 시 호출됨
    @PostMapping("/joinProc")
    public ResponseEntity<?> joinProc(@ModelAttribute JoinDTO joinDTO, BindingResult result){

        JoinValidator joinValidator = new JoinValidator();
        joinValidator.validate(joinDTO, result);

        userService.joinSave(joinDTO);

        return ResponseEntity.ok("가입 성공");
    }

    // 로그인 한 사용자 회원 탈퇴 (db에 업데이트 후 저장됨)
    @GetMapping("/quit")
    public String quit(@ModelAttribute UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long userId = userDetails.getIdx();
        userService.updateUserDeleted(userId);
        return "redirect:/login";
    }
}