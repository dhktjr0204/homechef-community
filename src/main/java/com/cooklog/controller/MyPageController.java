package com.cooklog.controller;

import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CustomUserDetails;
import com.cooklog.dto.MyPageUpdateRequestDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.service.BoardService;


import com.cooklog.service.CustomIUserDetailsService;
import com.cooklog.service.ImageService;
import com.cooklog.service.MyPageService;
import com.cooklog.service.MyPageServiceImpl;
import com.cooklog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/myPage")
public class MyPageController {

    private final UserService userService;
    private final ImageService imageService;
    private final MyPageService myPageService;
    private final CustomIUserDetailsService userDetailsService;
    private final BoardService boardService;
    private final CustomIUserDetailsService userDetailsService;

    // 마이페이지
    @GetMapping("/main")
    public String getMyPage(Model model){
        UserDTO userDTO1 = userDetailsService.getCurrentUserDTO();

        // 현재 인증된 사용자의 정보를 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 현 사용자의 idx 값으로 이외 정보들 가져오기
        UserDTO userDTO = userService.findUserById(userDetails.getIdx());
        String nickname = userDTO.getNickname();
        String introduction = userDTO.getIntroduction();
        String profileImage = userDTO.getProfileImageName();

        // 모델에 사용자 정보 추가
        model.addAttribute("currentLoginUser", userDTO1);
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
    @GetMapping("/basicProfile")
    public ResponseEntity<?> getBasicProfileUrl(){
        String basicProfileUrl=null;
        try {
            basicProfileUrl=imageService.fileLoad("images/db181dbe-7139-4f6c-912f-a53f12de6789_기본프로필.png");
        } catch (FileNotFoundException e) {
            basicProfileUrl="";
        }
        return ResponseEntity.ok(basicProfileUrl);
    }

    @GetMapping("/edit/{userId}")
    public String getProfileEditForm(@PathVariable Long userId, Model model) {
        UserDTO userDTO = userDetailsService.getCurrentUserDTO();

        if(!userId.equals(userDTO.getIdx())){
            return "error/404";
        }

        model.addAttribute("currentLoginUser", userDTO);
        return "myPage/profileEditForm";
    }

    @PutMapping("/edit/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId,
                                MyPageUpdateRequestDTO myPageUpdateRequestDTO,
                                @RequestPart(value = "newImage", required = false) MultipartFile updateProfileImage){
        UserDTO userDTO = userDetailsService.getCurrentUserDTO();

        //만약 인증되지 않은 사용자라면 예외처리
        if (!userId.equals(userDTO.getIdx())) {
            throw new NotValidateUserException();
        }
        myPageService.updateUserProfile(userId, myPageUpdateRequestDTO, updateProfileImage);

        return ResponseEntity.ok("/");
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
        Long currentUserIdx = userDetailsService.getUserIdx();
        List<BoardDTO> boardDTOList = userService.getBookmarkBoards(currentUserIdx);

        return ResponseEntity.ok(boardDTOList);
    }
}

