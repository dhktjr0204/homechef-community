package com.cooklog.service;

import com.cooklog.dto.MyPageUpdateRequestDTO;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.User;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.FollowRepository;
import com.cooklog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class MyPageServiceImplTest {
    @Mock
    private ImageService imageService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private MyPageServiceImpl myPageService;

    private MyPageUpdateRequestDTO createDummyMyPageUpdateDTO(String nickname, String introduction, String originImage){
        MyPageUpdateRequestDTO requestDTO=new MyPageUpdateRequestDTO();
        requestDTO.setNickname(nickname);
        requestDTO.setIntroduction(introduction);
        requestDTO.setOriginalImage(originImage);

        return requestDTO;
    }

    private User createUser(String profileImage){
        User user = User.builder().profileImage(profileImage).build();
        return user;
    }

    @Test
    @DisplayName("새로 등록된 사진이 없을때 수정 테스트")
    void updateUserProfile_ValidNotNewImage() throws IOException {
        //given
        Long userId=1L;
        MyPageUpdateRequestDTO requestDTO = createDummyMyPageUpdateDTO("user", "introduction", "image/test.png");
        User user= mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

        //when
        myPageService.updateUserProfile(userId, requestDTO, null);

        // then
        //s3에 사진 저장 안하는 지 확인
        verify(imageService,never()).fileWrite(any(),eq(userId));
        //s3에 사진 삭제 안하는지 확인
        verify(imageService, never()).deleteS3(any());
        // update 메서드에서 기존 사진 저장하는지 확인
        verify(user, times(1)).update(requestDTO.getNickname(), requestDTO.getIntroduction(), requestDTO.getOriginalImage());
    }

    @Test
    @DisplayName("새로 등록된 사진이 있을때 수정 테스트")
    void updateUserProfile_ValidNewImage() throws IOException {
        //given
        Long userId=1L;
        MyPageUpdateRequestDTO requestDTO = createDummyMyPageUpdateDTO("user", "introduction", "image/test.png");
        User user= mock(User.class);
        MockMultipartFile newImage = new MockMultipartFile("images", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(user.getProfileImage()).thenReturn("image/test.png");
        when(imageService.fileWrite(newImage,userId)).thenReturn("image/s3Image.png");

        //when
        myPageService.updateUserProfile(userId, requestDTO, newImage);

        // then
        // s3에 사진 저장하는 지 확인
        verify(imageService,times(1)).fileWrite(newImage,userId);
        // s3에 사진 삭제하는지 확인
        verify(imageService, times(1)).deleteS3(user.getProfileImage());
        // update 메서드에서 새로운 사진 DB에 저장하는지 확인
        verify(user, times(1)).update(requestDTO.getNickname(), requestDTO.getIntroduction(), "image/s3Image.png");
    }

    @Test
    @DisplayName("기본 프로필에서 새로운 사진으로 바꿀 때 수정 테스트")
    void updateUserProfile_ValidBasicImageToNewImage() throws IOException {
        //given
        Long userId=1L;
        MyPageUpdateRequestDTO requestDTO = createDummyMyPageUpdateDTO("user", "introduction", "images/db181dbe-7139-4f6c-912f-a53f12de6789_기본프로필.png");
        User user= mock(User.class);
        MockMultipartFile newImage = new MockMultipartFile("images", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(user.getProfileImage()).thenReturn("images/db181dbe-7139-4f6c-912f-a53f12de6789_기본프로필.png");
        when(imageService.fileWrite(newImage,userId)).thenReturn("image/s3Image.png");

        //when
        myPageService.updateUserProfile(userId, requestDTO, newImage);

        // then
        // s3에 사진 저장하는 지 확인
        verify(imageService,times(1)).fileWrite(newImage,userId);
        // s3에 사진 삭제 안하는지 확인
        verify(imageService, never()).deleteS3(user.getProfileImage());
        // update 메서드에서 새로운 사진 DB에 저장하는지 확인
        verify(user, times(1)).update(requestDTO.getNickname(), requestDTO.getIntroduction(), "image/s3Image.png");
    }

    @Test
    @DisplayName("해당 유저가 없을 때 수정 테스트")
    void updateUserProfile_ValidUser(){
        //given
        MyPageUpdateRequestDTO requestDTO = createDummyMyPageUpdateDTO("user", "introduction", "image/test.png");

        Long userId=1L;
        when(userRepository.findById(1L)).thenThrow(NotValidateUserException.class);

        //when, then
        assertThatThrownBy(()-> myPageService.updateUserProfile(userId, requestDTO, null))
                .isInstanceOf(NotValidateUserException.class);
    }



}