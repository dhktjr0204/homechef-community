package com.cooklog.controller;


import com.cooklog.dto.*;

import com.cooklog.model.Board;
import com.cooklog.model.Image;
import com.cooklog.service.BoardService;

import com.cooklog.service.CustomIUserDetailsService;
import com.cooklog.service.ImageService;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.service.BoardService;


import com.cooklog.service.CustomIUserDetailsService;
import com.cooklog.service.ImageService;
import com.cooklog.service.MyPageService;
import com.cooklog.service.MyPageServiceImpl;

import com.cooklog.service.UserService;
import com.cooklog.validate.BoardUpdateValidator;
import com.cooklog.validate.ProfileUpdateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/myPage")
public class MyPageController {

    private final ImageService imageService;
    private final MyPageService myPageService;
    private final CustomIUserDetailsService userDetailsService;

    // 마이페이지
    @GetMapping("/main/{id}")
    public String getMyPage(@PathVariable Long id, Model model) {

        // 현재 로그인 된 유저 정보 가져오기
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        // 팔로우한 갯수 가져오기
        MyPageFollowCountDTO followCountDTO = myPageService.getFollowCountDTO(id, loginUserDTO.getIdx());

        // 사용자가 작성한 게시물 리스트 가져오기
        List<MyPageDTO> myPageDTOS = myPageService.getBoardByUserId(id);
        UserDTO userDTO = myPageService.getUserDTO(id);

        // 모델에 사용자 정보 추가
        model.addAttribute("currentLoginUser", loginUserDTO);
        model.addAttribute("boards", myPageDTOS);
        model.addAttribute("user", userDTO);
        model.addAttribute("followCount", followCountDTO);

        return "myPage/myPage";
    }

    // 회원 정보 수정 페이지
    @GetMapping("/basicProfile")
    public ResponseEntity<?> getBasicProfileUrl() {
        String basicProfileUrl = imageService.fileLoad("images/db181dbe-7139-4f6c-912f-a53f12de6789_기본프로필.png");
        return ResponseEntity.ok(basicProfileUrl);
    }

    @GetMapping("/edit/{userId}")
    public String getProfileEditForm(@PathVariable Long userId, Model model) {
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        if (!userId.equals(loginUserDTO.getIdx()) || loginUserDTO.isDeleted()) {
            return "error/404";
        }

        model.addAttribute("currentLoginUser", loginUserDTO);
        return "myPage/profileEditForm";
    }

    @PutMapping("/edit/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId,
                                           MyPageUpdateRequestDTO myPageUpdateRequestDTO,
                                           @RequestPart(value = "newImage", required = false) MultipartFile updateProfileImage,
                                           BindingResult result) {
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        //만약 인증되지 않은 사용자라면 예외처리
        if (!userId.equals(loginUserDTO.getIdx()) || loginUserDTO.isDeleted()) {
            throw new NotValidateUserException();
        }

        //저장할 요소들 범위 넘지 않는지 확인하는 validate
        ProfileUpdateValidator profileUpdateValidator=new ProfileUpdateValidator();
        profileUpdateValidator.validate(myPageUpdateRequestDTO, result);

        myPageService.updateUserProfile(userId, myPageUpdateRequestDTO, updateProfileImage);

        return ResponseEntity.ok("/myPage/main/" + userId);
    }

    @GetMapping("/myBookmarks")
    public ResponseEntity<?> getMyBookmarks() {
        Long currentUserIdx = userDetailsService.getUserIdx();
        List<MyPageDTO> bookmarkBoards = myPageService.getBookmarkBoards(currentUserIdx);

        return ResponseEntity.ok(bookmarkBoards);
    }
}

