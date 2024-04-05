package com.cooklog.controller;

import com.cooklog.dto.MyPageUpdateRequestDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.service.CustomIUserDetailsService;
import com.cooklog.service.ImageService;
import com.cooklog.service.MyPageService;
import com.cooklog.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(MyPageController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MyPageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private ImageService imageService;
    @MockBean
    private MyPageService myPageService;
    @MockBean
    private CustomIUserDetailsService userDetailsService;

    //multipart요청 PUT요청으로 바꾸기
    private MockMultipartHttpServletRequestBuilder makePutMapping(String url) {
        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart(url);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        return builder;
    }

    private MyPageUpdateRequestDTO createDummyMyPageUpdateDTO(String nickname, String introduction, String originImage){
        MyPageUpdateRequestDTO requestDTO=new MyPageUpdateRequestDTO();
        requestDTO.setNickname(nickname);
        requestDTO.setIntroduction(introduction);
        requestDTO.setOriginalImage(originImage);

        return requestDTO;
    }
    @Test
    @DisplayName("프로필 수정 테스트")
    void updateProfile() throws Exception {
        //given
        //프로필 수정할 유저 Id
        Long userId=1L;

        MyPageUpdateRequestDTO requestDTO = createDummyMyPageUpdateDTO("user1", "test", "image/test.png");
        MockMultipartFile newImage = new MockMultipartFile("images", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));

        // Mocking userDTO
        //현재 로그인된 유저 Id
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L); // 가짜 user idx 값 설정
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        MockMultipartHttpServletRequestBuilder builder = makePutMapping("/myPage/edit/" + userId);

        ResultActions resultActions = mockMvc.perform(builder
                .file(newImage)
                .param("userId", String.valueOf(userId))
                .param("nickname", requestDTO.getNickname())
                .param("introduction", requestDTO.getIntroduction())
                .param("originalImage", requestDTO.getOriginalImage())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("다른 유저가 프로필 수정 요청했을 때 테스트")
    void updateProfile_withValidUserId() throws Exception {
        //given
        //프로필 수정할 유저 Id
        Long userId=1L;

        MyPageUpdateRequestDTO requestDTO = createDummyMyPageUpdateDTO("user1", "test", "image/test.png");
        MockMultipartFile newImage = new MockMultipartFile("images", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));

        // Mocking userDTO
        //현재 로그인된 유저 Id
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(2L); // 가짜 user idx 값 설정
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        MockMultipartHttpServletRequestBuilder builder = makePutMapping("/myPage/edit/" + userId);

        ResultActions resultActions = mockMvc.perform(builder
                .file(newImage)
                .param("userId", String.valueOf(userId))
                .param("nickname", requestDTO.getNickname())
                .param("introduction", requestDTO.getIntroduction())
                .param("originalImage", requestDTO.getOriginalImage())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        String expectedMessage="인증되지 않은 사용자입니다.";

        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().string(expectedMessage));
    }

    @Test
    @DisplayName("탈퇴 유저가 프로필 수정 요청했을 때 테스트")
    void updateProfile_withValidDeleteUser() throws Exception {
        //given
        //프로필 수정할 유저 Id
        Long userId=1L;

        MyPageUpdateRequestDTO requestDTO = createDummyMyPageUpdateDTO("user1", "test", "image/test.png");
        MockMultipartFile newImage = new MockMultipartFile("images", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));

        // Mocking userDTO
        //현재 로그인된 유저 Id
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(2L);// 가짜 user idx 값 설정
        userDTO.setDeleted(true);
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        MockMultipartHttpServletRequestBuilder builder = makePutMapping("/myPage/edit/" + userId);

        ResultActions resultActions = mockMvc.perform(builder
                .file(newImage)
                .param("userId", String.valueOf(userId))
                .param("nickname", requestDTO.getNickname())
                .param("introduction", requestDTO.getIntroduction())
                .param("originalImage", requestDTO.getOriginalImage())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        String expectedMessage="인증되지 않은 사용자입니다.";

        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().string(expectedMessage));
    }

    @Test
    @DisplayName("nickname 길이가 초과 했을 때 수정 테스트")
    void updateProfile_withValidNickNameLength() throws Exception {
        //given
        Long userId=1L;

        MyPageUpdateRequestDTO requestDTO = createDummyMyPageUpdateDTO("user1user1user1".repeat(5), "test", "image/test.png");
        MockMultipartFile newImage = new MockMultipartFile("images", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L);// 가짜 user idx 값 설정
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        MockMultipartHttpServletRequestBuilder builder = makePutMapping("/myPage/edit/" + userId);

        ResultActions resultActions = mockMvc.perform(builder
                .file(newImage)
                .param("userId", String.valueOf(userId))
                .param("nickname", requestDTO.getNickname())
                .param("introduction", requestDTO.getIntroduction())
                .param("originalImage", requestDTO.getOriginalImage())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        String expectedMessage="ID가 길이를 초과하였습니다. 20자 이하로 입력해 주세요.";

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(expectedMessage));
    }

    @Test
    @DisplayName("닉네임에 아무 값이 없을 때 수정 테스트")
    void updateProfile_withValidNickNameIsEmpty() throws Exception {
        //given
        Long userId=1L;

        MyPageUpdateRequestDTO requestDTO = createDummyMyPageUpdateDTO("", "test", "image/test.png");
        MockMultipartFile newImage = new MockMultipartFile("images", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L);// 가짜 user idx 값 설정
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        MockMultipartHttpServletRequestBuilder builder = makePutMapping("/myPage/edit/" + userId);

        ResultActions resultActions = mockMvc.perform(builder
                .file(newImage)
                .param("userId", String.valueOf(userId))
                .param("nickname", requestDTO.getNickname())
                .param("introduction", requestDTO.getIntroduction())
                .param("originalImage", requestDTO.getOriginalImage())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        String expectedMessage="ID를 입력해주세요.";

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(expectedMessage));
    }

    @Test
    @DisplayName("introduction 길이가 초과 했을 때 수정 테스트")
    void updateProfile_withValidIntrodcutionLength() throws Exception {
        //given
        Long userId=1L;

        MyPageUpdateRequestDTO requestDTO = createDummyMyPageUpdateDTO("user1", "testtesttesttesttest".repeat(16), "image/test.png");
        MockMultipartFile newImage = new MockMultipartFile("images", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L);// 가짜 user idx 값 설정
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        MockMultipartHttpServletRequestBuilder builder = makePutMapping("/myPage/edit/" + userId);

        ResultActions resultActions = mockMvc.perform(builder
                .file(newImage)
                .param("userId", String.valueOf(userId))
                .param("nickname", requestDTO.getNickname())
                .param("introduction", requestDTO.getIntroduction())
                .param("originalImage", requestDTO.getOriginalImage())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        String expectedMessage="인삿말이 길이를 초과하였습니다.";

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(expectedMessage));
    }
}