package com.cooklog.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cooklog.model.Role;
import com.cooklog.model.User;
import com.cooklog.service.CustomIUserDetailsService;
import com.cooklog.service.LikesService;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

//컨트롤러를 테스트 하기 위해서는 HTTP 호출이 필요. @WebMvcTest를 이용하면 MockMvc객체가 자동으로 생성되고 테스트에 필요한 요소들을 빈으로 등록한다.
@WebMvcTest(LikesController.class)
@ExtendWith(MockitoExtension.class)
//jpa 메타모델(엔티티 클래스와 그 속성들)을 만들어 메타데이터 형태로 관리한다.
@MockBean(JpaMetamodelMappingContext.class)
class LikesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private LikesController likesController;

    //WebMvcTest,SpringBootTest와 같이 Spring컨텍스트를 로드하는 테스트에서는 @MockBean사용
    @MockBean
    private LikesService likesService;

    @MockBean
    private CustomIUserDetailsService userDetailsService;

    private User createUser(Long userIdx) {
        User user = new User(userIdx,"hanju","test@test","test","안녕하세요", Role.USER,"프로필이미지url",0,false,new ArrayList<>());
        return user;
    }

    @Test
    @DisplayName("addLike테스트")
    //해당 메서드 내에서 발생할 수 있는 예외를 처리하기 위해 throws Exception작성
    public void addLikeTest() throws Exception{
        //given
        Long boardId = 1L;
        User currentUser = createUser(1L);
        Long returnLikesNum  = 10L;

        when(userDetailsService.isValidCurrentUser()).thenReturn(currentUser);
        when(likesService.getNumberOfLikesByBoardId(boardId)).thenReturn(returnLikesNum);

        //when,then
        mockMvc.perform(post("/api/likes/add")
            .param("boardId",Long.toString(boardId)))
            .andExpect(status().isOk())
            .andExpect(content().string(String.valueOf(returnLikesNum)));

        verify(likesService).addLike(currentUser,boardId);
        verify(likesService).getNumberOfLikesByBoardId(boardId);
    }

    @Test
    @DisplayName("cancelLike 테스트")
    public void cancelLikeTest() throws Exception {
        //given
        Long boardId = 1L;
        User currentUser = createUser(1L);
        Long returnLikesNum  = 9L;

        when(userDetailsService.isValidCurrentUser()).thenReturn(currentUser);
        when(likesService.getNumberOfLikesByBoardId(boardId)).thenReturn(returnLikesNum);

        //when,then
        mockMvc.perform(delete("/api/likes/cancel")
                .param("boardId",Long.toString(boardId)))
            .andExpect(status().isOk())
            .andExpect(content().string(String.valueOf(returnLikesNum)));

        verify(likesService).cancelLike(currentUser,boardId);
        verify(likesService).getNumberOfLikesByBoardId(boardId);
    }
}