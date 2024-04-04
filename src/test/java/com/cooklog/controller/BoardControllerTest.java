package com.cooklog.controller;

import com.cooklog.dto.BoardCreateRequestDTO;
import com.cooklog.dto.BoardUpdateRequestDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.model.Board;
import com.cooklog.service.BoardService;
import com.cooklog.service.CommentService;
import com.cooklog.service.CustomIUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(BoardController.class)
@MockBean(JpaMetamodelMappingContext.class)
class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private CustomIUserDetailsService userDetailsService;

    private BoardCreateRequestDTO createDummyBoardCreateDTO(String content, List<String> tags) {
        BoardCreateRequestDTO requestDTO = new BoardCreateRequestDTO();
        requestDTO.setContent(content);
        requestDTO.setTags(tags);

        return requestDTO;
    }

    private BoardUpdateRequestDTO createDummyBoardUpdateDTO(Long userId, String content, List<String> tags, List<String> originalImageNames) {
        BoardUpdateRequestDTO requestDTO = new BoardUpdateRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setContent(content);
        requestDTO.setTags(tags);
        requestDTO.setImageUrls(originalImageNames);

        return requestDTO;
    }

    private List<MultipartFile> createDummyImageList() {
        MockMultipartFile multipartFile1 = new MockMultipartFile("images", "newFile.jpg", "image/png", "test file".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile multipartFile2 = new MockMultipartFile("images", "newFile2.jpg", "image/png", "test file2".getBytes(StandardCharsets.UTF_8));
        List<MultipartFile> images = Arrays.asList(multipartFile1, multipartFile2);

        return images;
    }

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

    //저장 테스트
    @Test
    @DisplayName("저장테스트")
    void saveBoard() throws Exception {
        //given
        BoardCreateRequestDTO requestDTO = createDummyBoardCreateDTO("Test Content", Arrays.asList("tag1", "tag2"));
        List<MultipartFile> images = createDummyImageList();

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L); // 가짜 user idx 값 설정
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //Mocking Board
        Board board = Board.builder()
                .id(1L).build();// 가짜 ID 값 설정
        when(boardService.save(eq(userDTO.getIdx()), any(BoardCreateRequestDTO.class), eq(images))).thenReturn(board);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/board/write")
                .file((MockMultipartFile) images.get(0))
                .file((MockMultipartFile) images.get(1))
                .param("content", requestDTO.getContent())
                .param("tags", requestDTO.getTags().get(0))
                .param("tags", requestDTO.getTags().get(1))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("tag 개수가 넘칠때 저장 테스트")
    void saveBoard_withValidTagCount() throws Exception {
        //given
        BoardCreateRequestDTO requestDTO = createDummyBoardCreateDTO("Test Content", Arrays.asList("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10", "tag11"));
        List<MultipartFile> images = createDummyImageList();

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L); // 가짜 user idx 값 설정
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //Mocking Board
        Board board = Board.builder()
                .id(1L).build();// 가짜 ID 값 설정
        when(boardService.save(eq(userDTO.getIdx()), any(BoardCreateRequestDTO.class), eq(images))).thenReturn(board);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/board/write")
                .file((MockMultipartFile) images.get(0))
                .file((MockMultipartFile) images.get(1))
                .param("content", requestDTO.getContent())
                .param("tags", requestDTO.getTags().get(0))
                .param("tags", requestDTO.getTags().get(1))
                .param("tags", requestDTO.getTags().get(2))
                .param("tags", requestDTO.getTags().get(3))
                .param("tags", requestDTO.getTags().get(4))
                .param("tags", requestDTO.getTags().get(5))
                .param("tags", requestDTO.getTags().get(6))
                .param("tags", requestDTO.getTags().get(7))
                .param("tags", requestDTO.getTags().get(8))
                .param("tags", requestDTO.getTags().get(9))
                .param("tags", requestDTO.getTags().get(10))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        String expectedMessage = "태그가 최대 개수를 초과하였습니다.";

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(expectedMessage));
    }

    @Test
    @DisplayName("tag 길이가 넘칠때 테스트")
    void saveBoard_withValidTagLength() throws Exception {
        //given
        BoardCreateRequestDTO requestDTO = createDummyBoardCreateDTO("Test Content", List.of("testtagtesttagtesttagtesttag".repeat(10)));
        List<MultipartFile> images = createDummyImageList();

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L); // 가짜 user idx 값 설정
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //Mocking Board
        Board board = Board.builder()
                .id(1L).build();// 가짜 ID 값 설정
        when(boardService.save(eq(userDTO.getIdx()), any(BoardCreateRequestDTO.class), eq(images))).thenReturn(board);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/board/write")
                .file((MockMultipartFile) images.get(0))
                .file((MockMultipartFile) images.get(1))
                .param("content", requestDTO.getContent())
                .param("tags", requestDTO.getTags().get(0))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        String expectedMessage = "태그 길이가 최대 길이를 초과하였습니다. 태크 길이를 줄여주세요.";

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(expectedMessage));
    }

    @Test
    @DisplayName("content 길이가 넘칠때 테스트")
    void saveBoard_withValidContentLength() throws Exception {
        //given
        BoardCreateRequestDTO requestDTO = createDummyBoardCreateDTO("Test Content Test Content".repeat(21), List.of("tag"));
        List<MultipartFile> images = createDummyImageList();

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L); // 가짜 user idx 값 설정
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //Mocking Board
        Board board = Board.builder()
                .id(1L).build();// 가짜 ID 값 설정
        when(boardService.save(eq(userDTO.getIdx()), any(BoardCreateRequestDTO.class), eq(images))).thenReturn(board);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/board/write")
                .file((MockMultipartFile) images.get(0))
                .file((MockMultipartFile) images.get(1))
                .param("content", requestDTO.getContent())
                .param("tags", requestDTO.getTags().get(0))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        String expectedMessage = "콘텐츠 길이가 최대 길이를 초과하였습니다.";

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(expectedMessage));
    }

    @Test
    @DisplayName("Content와 tag가 빈값 일 때 테스트")
    void saveBoard_withValidContentAndTagsIsNull() throws Exception {
        //given
        List<MultipartFile> images = createDummyImageList();

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L); // 가짜 user idx 값 설정
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //Mocking Board
        Board board = Board.builder()
                .id(1L).build();// 가짜 ID 값 설정
        when(boardService.save(eq(userDTO.getIdx()), any(BoardCreateRequestDTO.class), eq(images))).thenReturn(board);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/board/write")
                .file((MockMultipartFile) images.get(0))
                .file((MockMultipartFile) images.get(1))
                .param("content", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
    //저장 테스트 끝

    //수정 테스트
    @Test
    @DisplayName("수정 테스트")
    void updateBoard() throws Exception {
        //given
        Long userId = 1L;

        BoardUpdateRequestDTO requestDTO = createDummyBoardUpdateDTO(1L, "Test Content",
                Arrays.asList("tag1", "tag2"),
                Arrays.asList("image/test1.png", "imge/test2.png"));
        List<MultipartFile> images = createDummyImageList();

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L); // 가짜 user idx 값 설정
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        MockMultipartHttpServletRequestBuilder builder = makePutMapping("/board/edit/" + userId);

        ResultActions resultActions = mockMvc.perform(builder
                .file((MockMultipartFile) images.get(0))
                .file((MockMultipartFile) images.get(1))
                .param("userId", String.valueOf(userId))
                .param("content", requestDTO.getContent())
                .param("tags", requestDTO.getTags().get(0))
                .param("tags", requestDTO.getTags().get(1))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("글 쓴 유저가 아닌 다른 유저가 수정 요청 했을 때 테스트")
    void updateBoard_withValidDeleteUser() throws Exception {
        //given
        Long userId = 1L;

        //업데이트할 게시물
        BoardUpdateRequestDTO requestDTO = createDummyBoardUpdateDTO(1L, "Test Content",
                Arrays.asList("tag1", "tag2"),
                Arrays.asList("image/test1.png", "imge/test2.png"));
        List<MultipartFile> images = createDummyImageList();

        // Mocking userDTO
        //현재 로그인 한 유저의 아이디를 2L로 설정
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(2L);// 가짜 user idx 값 설정
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        MockMultipartHttpServletRequestBuilder builder = makePutMapping("/board/edit/" + 1L);

        ResultActions resultActions = mockMvc.perform(builder
                .file((MockMultipartFile) images.get(0))
                .file((MockMultipartFile) images.get(1))
                .param("userId", String.valueOf(userId))
                .param("content", requestDTO.getContent())
                .param("tags", requestDTO.getTags().get(0))
                .param("tags", requestDTO.getTags().get(1))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        String expectedMessage = "인증되지 않은 사용자입니다.";

        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().string(expectedMessage));
    }

    @Test
    @DisplayName("탈퇴유저가 수정 요청 했을 때 테스트")
    void updateBoard_withValidUserId() throws Exception {
        //given
        Long userId = 1L;

        //업데이트할 게시물
        BoardUpdateRequestDTO requestDTO = createDummyBoardUpdateDTO(1L, "Test Content",
                Arrays.asList("tag1", "tag2"),
                Arrays.asList("image/test1.png", "imge/test2.png"));
        List<MultipartFile> images = createDummyImageList();

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L);
        userDTO.setDeleted(true);//유저가 탈퇴상태 일 때
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        MockMultipartHttpServletRequestBuilder builder = makePutMapping("/board/edit/" + 1L);

        ResultActions resultActions = mockMvc.perform(builder
                .file((MockMultipartFile) images.get(0))
                .file((MockMultipartFile) images.get(1))
                .param("userId", String.valueOf(userId))
                .param("content", requestDTO.getContent())
                .param("tags", requestDTO.getTags().get(0))
                .param("tags", requestDTO.getTags().get(1))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        String expectedMessage = "인증되지 않은 사용자입니다.";

        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().string(expectedMessage));
    }

    @Test
    @DisplayName("tag 길이가 넘칠때 수정 테스트")
    void updateBoard_withTagLength() throws Exception {
        //given
        Long userId = 1L;

        //업데이트할 게시물
        BoardUpdateRequestDTO requestDTO = createDummyBoardUpdateDTO(1L, "Test Content",
                List.of("testtagtesttagtesttagtesttag".repeat(10)),
                Arrays.asList("image/test1.png", "imge/test2.png"));
        List<MultipartFile> images = createDummyImageList();

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L);
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        MockMultipartHttpServletRequestBuilder builder = makePutMapping("/board/edit/" + 1L);

        ResultActions resultActions = mockMvc.perform(builder
                .file((MockMultipartFile) images.get(0))
                .file((MockMultipartFile) images.get(1))
                .param("userId", String.valueOf(userId))
                .param("content", requestDTO.getContent())
                .param("tags", requestDTO.getTags().get(0))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        String expectedMessage = "태그 길이가 최대 길이를 초과하였습니다. 태크 길이를 줄여주세요.";

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(expectedMessage));
    }

    @Test
    @DisplayName("content 길이가 넘칠때 수정 테스트")
    void updateBoard_withContentLength() throws Exception {
        //given
        Long userId = 1L;

        //업데이트할 게시물
        BoardUpdateRequestDTO requestDTO = createDummyBoardUpdateDTO(1L, "Test Content Test Content".repeat(21),
                List.of("test"),
                Arrays.asList("image/test1.png", "imge/test2.png"));
        List<MultipartFile> images = createDummyImageList();

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L);
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        MockMultipartHttpServletRequestBuilder builder = makePutMapping("/board/edit/" + 1L);

        ResultActions resultActions = mockMvc.perform(builder
                .file((MockMultipartFile) images.get(0))
                .file((MockMultipartFile) images.get(1))
                .param("userId", String.valueOf(userId))
                .param("content", requestDTO.getContent())
                .param("tags", requestDTO.getTags().get(0))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        String expectedMessage = "콘텐츠 길이가 최대 길이를 초과하였습니다.";

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(expectedMessage));
    }

    @Test
    @DisplayName("content와 tag 둘다 빈 값일 때 수정 테스트")
    void updateBoard_withContentAndTagsIsNull() throws Exception {
        //given
        Long userId = 1L;

        //업데이트할 게시물
        List<MultipartFile> images = createDummyImageList();

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L);
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        MockMultipartHttpServletRequestBuilder builder = makePutMapping("/board/edit/" + 1L);

        ResultActions resultActions = mockMvc.perform(builder
                .file((MockMultipartFile) images.get(0))
                .file((MockMultipartFile) images.get(1))
                .param("userId", String.valueOf(userId))
                .param("content", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
    //수정 테스트 끝

    //삭제 테스트
    @Test
    @DisplayName("삭제 테스트")
    void deleteBoard() throws Exception {
        //given
        Long userId = 1L;

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L);
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/board/delete/" + 1L)
                .param("userId", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("글쓴 유저가 아닌 다른 유저가 삭제 요청 했을 때 테스트")
    void deleteBoard_withValidUserId() throws Exception {
        //given
        //글쓴 유저 아이디 2L
        Long userId = 2L;

        // Mocking userDTO
        //현재 로그인 한 유저의 아이디를 1L로 설정
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L);
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/board/delete/" + 1L)
                .param("userId", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("탈퇴한 유저가 삭제 요청 했을 때 테스트")
    void deleteBoard_withValidDeleteUser() throws Exception {
        //given
        Long userId = 1L;

        // Mocking userDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setIdx(1L);
        userDTO.setDeleted(true);
        when(userDetailsService.getCurrentUserDTO()).thenReturn(userDTO);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/board/delete/" + 1L)
                .param("userId", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}