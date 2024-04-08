package com.cooklog.controller;

import com.cooklog.dto.*;
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
    @Operation(summary = "로그인 페이지를 조회할 수 있는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 HTML을 반환",
                    content = @Content(mediaType = "text/html"))
    })
    public String login(@RequestParam(value = "error", required = false)String error,
                        @RequestParam(value = "exception", required = false)String exception, Model model) {

        model.addAttribute("error", error);
        model.addAttribute("exception", exception);

        return "user/login";
    }

    //로그아웃
    @GetMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "회원의 세션을 무효화합니다. 메인 페이지로 돌아갑니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃시 메인 페이지 HTML 반환",
                    content = @Content(mediaType = "text/html"))
    })
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }

    //회원가입 뷰
    @GetMapping("/join")
    @Operation(summary = "회원가입 페이지를 조회할 수 있는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 HTML을 반환",
                    content = @Content(mediaType = "text/html"))
    })
    public String join() {
        return "user/join";
    }

    //회원가입 폼 처리 시 호출됨
    @Operation(summary = "회원가입 API", description = "회원의 계정을 DB에 저장하고 로그인 페이지로 돌아갑니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 시 로그인 페이지 HTML 반환",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name="가입 성공 처리", value ="가입 성공" ))),
    })
    @PostMapping("/joinProc")
    public ResponseEntity<?> joinProc(@ModelAttribute JoinDTO joinDTO, BindingResult result){

        JoinValidator joinValidator = new JoinValidator();
        joinValidator.validate(joinDTO, result);

        userService.joinSave(joinDTO);

        return ResponseEntity.ok("가입 성공");
    }

    // 로그인 한 사용자 회원 탈퇴 (db에 업데이트 후 저장됨)
    @GetMapping("/quit")
    @Operation(summary = "탈퇴 API", description = "회원의 계정을 탈퇴처리합니다. 로그인 페이지로 돌아갑니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "탈퇴 시 로그인 페이지 HTML 반환",
                    content = @Content(mediaType = "text/html"))
    })
    public String quit(@ModelAttribute UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long userId = userDetails.getIdx();
        userService.updateUserDeleted(userId);
        return "redirect:/login";
    }
}