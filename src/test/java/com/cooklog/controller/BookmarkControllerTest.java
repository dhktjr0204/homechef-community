package com.cooklog.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cooklog.model.Role;
import com.cooklog.model.User;
import com.cooklog.service.BookmarkService;
import com.cooklog.service.CustomIUserDetailsService;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

//BookmarkController에 대한 테스트를 활성화한다고 표시해주어야함
@WebMvcTest(BookmarkController.class)
@ExtendWith(MockitoExtension.class)
@MockBean(JpaMetamodelMappingContext.class)
class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookmarkService bookmarkService;

    @MockBean
    private CustomIUserDetailsService userDetailsService;

    private User createUser(Long userIdx) {
        User user = new User(userIdx,"hanju","test@test","test","안녕하세요", Role.USER,"프로필이미지url",0,false,new ArrayList<>());
        return user;
    }

    @Test
    @DisplayName("addMark 테스트")
    public void addMarkTest() throws Exception {
        //given
        Long boardId = 1L;
        User currentUser = createUser(1L);

        when(userDetailsService.isValidCurrentUser()).thenReturn(currentUser);

        //when,then
        mockMvc.perform(post("/api/bookmark/add")
                .param("boardId",Long.toString(boardId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookmarkService).addMark(currentUser,boardId);
    }

    @Test
    @DisplayName("cancelMark 테스트")
    public void cancelMarkTest() throws Exception {
        //given
        Long boardId = 1L;
        User currentUser = createUser(1L);

        when(userDetailsService.isValidCurrentUser()).thenReturn(currentUser);

        //when,then
        mockMvc.perform(delete("/api/bookmark/cancel")
                .param("boardId",Long.toString(boardId))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(bookmarkService).cancelMark(currentUser,boardId);
    }
}