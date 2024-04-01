package com.cooklog.controller;

import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CustomUserDetails;
import com.cooklog.dto.UserDTO;
import com.cooklog.service.BoardService;
import com.cooklog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/myPage")
public class MyPageController {

    private final UserService userService;
    private final BoardService boardService;

    // 마이페이지
    @GetMapping("/main")
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
        model.addAttribute("profileImage", profileImage);

        // 사용자가 작성한 게시물 목록 가져옴
//        List<BoardDTO> currentUserBoards = boardService.findBoardsByUserId(userDetails.getIdx());

        // 사용자가 작성한 게시물 갯수 가져옴
        Long boardCnt = userService.getNumberOfBoardByUserId(userDTO.getIdx());

        model.addAttribute("boardCnt",boardCnt);

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


}

