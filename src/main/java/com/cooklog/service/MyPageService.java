package com.cooklog.service;

import com.cooklog.dto.MyPageDTO;
import com.cooklog.dto.MyPageFollowCountDTO;
import com.cooklog.dto.MyPageUpdateRequestDTO;
import com.cooklog.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MyPageService {
    void updateUserProfile(UserDTO loginUserDTO, MyPageUpdateRequestDTO myPageUpdateRequestDTO, MultipartFile newImageFile);
    // 사용자가 작성한 게시물 리스트를 가져오는 메소드
    List<MyPageDTO> getBoardByUserId(Long userIdx);

    // 사용자 프로필 이미지 URL 생성 메소드
    UserDTO getUserDTO(Long userIdx);

    // 로그인 한 사용자의 팔로우 팔로워 수 가져오는 메소드
    MyPageFollowCountDTO getFollowCountDTO(Long userIdx, Long loginUserId);
}
