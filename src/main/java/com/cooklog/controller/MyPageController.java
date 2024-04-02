package com.cooklog.controller;

import com.cooklog.dto.*;

import com.cooklog.model.Board;
import com.cooklog.model.Image;
import com.cooklog.service.BoardService;

import com.cooklog.service.CustomIUserDetailsService;
import com.cooklog.service.ImageService;
import com.cooklog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/myPage")
public class MyPageController {

    private final UserService userService;
    private final CustomIUserDetailsService userDetailsService;
    private final ImageService imageService;
    private final BoardService boardService;

    // 마이페이지
    @GetMapping("/main/{id}")
    public String getMyPage(@PathVariable Long id, Model model) throws FileNotFoundException {

        // 현재 로그인 된 유저 정보 가져오기
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        // 팔로우한 갯수 가져오기
        MyPageFollowCountDTO followCountDTO = userService.getFollowCountDTO(id, loginUserDTO.getIdx());

        // 사용자가 작성한 게시물 리스트 가져오기
        List<MyPageDTO> myPageDTOS = userService.getBoardByUserId(id);
        UserDTO userDTO = userService.getUserDTO(id);


        // 모델에 사용자 정보 추가
        model.addAttribute("currentLoginUser", loginUserDTO);
        model.addAttribute("boards",myPageDTOS);
        model.addAttribute("user",userDTO);
        model.addAttribute("followCount",followCountDTO);

        return "myPage/myPage";
    }

    // 팔로워 페이지
    @GetMapping("/follower")
    public String getFollower(){
        return "myPage/followerPage";
    }

    // 회원 정보 수정 페이지
    @GetMapping("/edit")
    public String getProfileEditForm(){
        return "myPage/profileEditForm";
    }


//    // 회원 정보 수정 폼
//    @PutMapping("/edit")
//    public ResponseEntity<String> edit(@RequestParam("userIdx") Long userIdx,
//                                       @ResponseBody UserUpdateRequestDTO userUpdateRequestDTO) {
//
//        userService.updateUserProfile(userIdx, userUpdateRequestDTO);
//        return ResponseEntity.ok("/myPage/edit");
//    }

    @GetMapping("/bookmark")
    public ResponseEntity<?> getMyBookmarks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userIdx = userDetails.getIdx();

        List<BoardDTO> boardDTOList = userService.getBookmarkBoards(userIdx);

        return ResponseEntity.ok(boardDTOList);
    }
}

