package com.cooklog.controller;

import com.cooklog.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    // 마이페이지
    @GetMapping("/myPage")
    public String getMyPage(Model model){

        // 현재 인증된 사용자의 정보를 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 현 사용자의 idx 값으로 이외 정보들 가져오기
        UserDTO userDTO = userService.findUserById(userDetails.getIdx());
        String nickname = userDTO.getNickname();
        String introduction = userDTO.getIntroduction();
        String profileImage = userDTO.getProfileImage();

        // 모델에 사용자 정보 추가
        model.addAttribute("nickname", nickname);
        model.addAttribute("introduction", introduction);
        model.addAttribute("profileImageUrl", profileImage);

        return "myPage/myPage";
    }

    // 팔로워 페이지
    @GetMapping("/myPage/follower")
    public String getFollower(){
        return "myPage/followerPage";
    }

    // 회원 정보 수정 페이지
    @GetMapping("/myPage/edit")
    public String getProfileEditForm(){
        return "myPage/profileEditForm";
    }

//    // 회원 정보 수정 폼
//    @PutMapping("/myPage/edit")
//    public ResponseEntity<String> edit(@RequestParam("userIdx") Long userIdx,
//                                       @ResponseBody UserUpdateRequestDTO userUpdateRequestDTO) {
//
//        userService.updateUserProfile(userIdx, userUpdateRequestDTO);
//        return ResponseEntity.ok("/myPage/edit");
//    }


}
