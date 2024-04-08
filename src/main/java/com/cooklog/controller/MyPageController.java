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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "마이페이지 조회 API", description = "사용자의 마이페이지를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "마이페이지 HTML 페이지 반환",
                content = @Content(mediaType = "text/html"))
    })
    public String getMyPage(@PathVariable Long id, Model model) {

        // 현재 로그인 된 유저 정보 가져오기
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        // 팔로우한 갯수 가져오기
        MyPageFollowCountDTO followCountDTO = myPageService.getFollowCountDTO(id, loginUserDTO.getIdx());

        // 마이페이지 소유자가 작성한 게시물 리스트 가져오기
        List<MyPageDTO> myPageDTOS = myPageService.getBoardByUserId(id);
        // 마이페이지 소유자 정보
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
    @Operation(summary = "프로필을 수정할 수 있는 페이지 조회 API", description = "유저가 프로필을 수정할 수 있는 페이지로 이동하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 form HTML반환",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 유저 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name="인증되지 않은 유저 오류", value ="인증되지 않은 사용자입니다" )))
    })
    public String getProfileEditForm(@PathVariable Long userId, Model model) {
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        if (!userId.equals(loginUserDTO.getIdx()) || loginUserDTO.isDeleted()) {
            return "error/404";
        }

        model.addAttribute("currentLoginUser", loginUserDTO);
        return "myPage/profileEditForm";
    }

    @PutMapping("/edit/{userId}")
    @Operation(summary = "프로필을 수정할 수 있는 API", description = "유저가 프로필을 수정하면 DB에 저장하고 마이페이지로 이동하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "마이페이지 HTML반환",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 유저 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name="인증되지 않은 유저 오류", value ="인증되지 않은 사용자입니다" ))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력 값",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name="프로필 ID 입력 오류", value ="ID를 입력해주세요." ),
                                    @ExampleObject(name="프로필 ID 입력 오류", value ="ID가 길이를 초과하였습니다. 20자 이하로 입력해 주세요." ),
                                    @ExampleObject(name="인삿말 입력 오류", value ="인삿말이 길이를 초과하였습니다." )
                            }))
    })
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

        myPageService.updateUserProfile(loginUserDTO, myPageUpdateRequestDTO, updateProfileImage);

        return ResponseEntity.ok("/myPage/main/" + userId);
    }

    @GetMapping("/myBookmarks")
    public ResponseEntity<?> getMyBookmarks() {
        Long currentUserIdx = userDetailsService.getUserIdx();
        List<MyPageDTO> bookmarkBoards = myPageService.getBookmarkBoards(currentUserIdx);

        return ResponseEntity.ok(bookmarkBoards);
    }
}

