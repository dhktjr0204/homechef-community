package com.cooklog.controller;

import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CustomUserDetails;
import com.cooklog.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final UserService userService;

    @GetMapping("")
    public String getMyPage(){
        return "myPage/myPage";
    }

    @GetMapping("/follower")
    public String getFollower(){
        return "myPage/followerPage";
    }

    @GetMapping("/edit")
    public String getProfileEditForm(){
        return "myPage/profileEditForm";
    }

    @GetMapping("/bookmark")
    public ResponseEntity<?> getMyBookmarks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userIdx = userDetails.getIdx();

        List<BoardDTO> boardDTOList = userService.getBookmarkBoards(userIdx);

        return ResponseEntity.ok(boardDTOList);
    }
}
