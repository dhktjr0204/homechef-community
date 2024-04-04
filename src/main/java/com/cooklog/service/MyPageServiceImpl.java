package com.cooklog.service;

import com.cooklog.dto.MyPageDTO;
import com.cooklog.dto.MyPageFollowCountDTO;
import com.cooklog.dto.MyPageUpdateRequestDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.Board;
import com.cooklog.model.Image;
import com.cooklog.model.User;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.FollowRepository;
import com.cooklog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final FollowRepository followRepository;

    private final String BASIC_IMAGE = "images/db181dbe-7139-4f6c-912f-a53f12de6789_기본프로필.png";

    @Transactional
    @Override
    public void updateUserProfile(Long userId,
                                  MyPageUpdateRequestDTO request,
                                  MultipartFile newImageFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(NotValidateUserException::new);

        if (newImageFile != null) {

            String saveS3FileName = imageService.fileWrite(newImageFile, userId);

            //s3에 등록된 기존 이미지 삭제, 기존 이미지가 기본이미지라면 삭제하지 않음
            if (!user.getProfileImage().equals(BASIC_IMAGE)) {
                imageService.deleteS3(user.getProfileImage());
            }

            user.update(request.getNickname(), request.getIntroduction(), saveS3FileName);
        } else {
            user.update(request.getNickname(), request.getIntroduction(), request.getOriginalImage());
        }
    }

    // 사용자가 작성한 게시물의 대표 이미지 URL을 가져옴
    @Override
    public List<MyPageDTO> getBoardByUserId(Long userIdx) {
        List<MyPageDTO> myPageDTOList = new ArrayList<>();
        List<Board> boardList = boardRepository.findByUserIdx(userIdx);

        for (Board board : boardList) {

            String boardImageUrl = imageService.fileLoad(board.getImages().get(0).getName());

            MyPageDTO myPageDTO = MyPageDTO.builder()
                    .id(board.getId())
                    .imageUrl(boardImageUrl).build();

            myPageDTOList.add(myPageDTO);
        }

        return myPageDTOList;
    }

    // 사용자 프로필 이미지 URL 생성 후 UserDTO 객체에 담는 메서드
    @Override
    public UserDTO getUserDTO(Long userIdx) {
        User user = userRepository.findById(userIdx).orElseThrow(NotValidateUserException::new);

        String profileImageUrl = imageService.fileLoad(user.getProfileImage());

        UserDTO userDTO = UserDTO.builder()
                .idx(user.getIdx())
                .nickname(user.getNickname())
                .introduction(user.getIntroduction())
                .profileImageUrl(profileImageUrl).build();

        return userDTO;
    }

    // 로그인 한 사용자의 팔로우-팔로워 수를 가져옴
    @Override
    public MyPageFollowCountDTO getFollowCountDTO(Long userIdx, Long loginUserId) {
        return followRepository.findFollowCountByUserId(userIdx, loginUserId);
    }
}
