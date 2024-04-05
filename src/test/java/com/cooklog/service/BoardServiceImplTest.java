package com.cooklog.service;

import com.cooklog.dto.BoardCreateRequestDTO;
import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.BoardUpdateRequestDTO;
import com.cooklog.exception.board.BoardNotFoundException;
import com.cooklog.exception.board.NoImageException;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.Board;
import com.cooklog.model.Image;
import com.cooklog.model.User;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.TagRepository;
import com.cooklog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class BoardServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private TagService tagService;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private BoardServiceImpl boardService;

    private Board createBoard(Long boardId) {
        Board board = Board.builder()
                .id(boardId)
                .content("test")
                .createdAt(LocalDateTime.now())
                .readCount(0)
                .user(new User())
                .images(new ArrayList<>())
                .tags(new ArrayList<>())
                .likes(new ArrayList<>())
                .bookmarks(new ArrayList<>()).build();

        return board;
    }

    private List<MultipartFile> createDummyImageList() {
        MockMultipartFile multipartFile1 = new MockMultipartFile("images", "newFile.jpg", "image/png", "test file".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile multipartFile2 = new MockMultipartFile("images", "newFile2.jpg", "image/png", "test file2".getBytes(StandardCharsets.UTF_8));
        List<MultipartFile> images = Arrays.asList(multipartFile1, multipartFile2);

        return images;
    }

    private BoardCreateRequestDTO createBoardCreateRequestDTO() {
        return BoardCreateRequestDTO.builder()
                .content("test")
                .tags(Arrays.asList("tag", "tag2")).build();
    }

    private BoardUpdateRequestDTO createUpdateRequestDTO(){
        return BoardUpdateRequestDTO.builder()
                .userId(1L)
                .content("test")
                .tags(Arrays.asList("tag","tag2"))
                .imageUrls(Arrays.asList("images/test.png","images/test2.png"))
                .build();
    }

    //조회 테스트
    @Test
    @DisplayName("메인 페이지 첫 요청 테스트")
    void getAllBoard_withFirstRequest() {
        //given
        Pageable pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());
        Long userId = 1L;
        Long lastBoardId = 0L;
        String sortType = "cratedAt: DESC";

        List<Board> boardList = Arrays.asList(createBoard(1L), createBoard(2L));
        Page<Board> boardPage = new PageImpl<>(boardList);
        when(boardRepository.findAll(pageable)).thenReturn(boardPage);

        //converBoard때 필요한 것
        when(imageService.fileLoad(any())).thenReturn("");
        when(imageService.fileListLoad(anyList())).thenReturn(new ArrayList<>());

        //when
        boardService.getAllBoard(pageable, userId, lastBoardId, sortType);

        //then
        verify(boardRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("메인 페이지 조회순 테스트")
    void getAllBoard_withSortByReadCnt() {
        //given
        Pageable pageable = PageRequest.of(0, 3, Sort.by("readCount").descending());
        Long userId = 1L;
        Long lastBoardId = 10L;
        String sortType = "readCount: DESC";

        List<Board> boardList = Arrays.asList(createBoard(1L), createBoard(2L));
        Page<Board> boardPage = new PageImpl<>(boardList);
        when(boardRepository.findAllOrderByReadCount(lastBoardId, pageable)).thenReturn(boardPage);

        //converBoard때 필요한 것
        when(imageService.fileLoad(any())).thenReturn("");
        when(imageService.fileListLoad(anyList())).thenReturn(new ArrayList<>());

        //when
        boardService.getAllBoard(pageable, userId, lastBoardId, sortType);

        //then
        //조회순 로직이 1번 쓰였는지 확인
        verify(boardRepository, times(1)).findAllOrderByReadCount(lastBoardId, pageable);
    }

    @Test
    @DisplayName("메인페이지에 등록된 게시물이 없을 때 테스트")
    void getAllBoard_withNoBoard() {
        //given
        Pageable pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());
        Long userId = 1L;
        Long lastBoardId = 0L;
        String sortType = "createdAt: DESC";

        List<Board> boardList = new ArrayList<>();
        Page<Board> boardPage = new PageImpl<>(boardList);
        when(boardRepository.findAll(pageable)).thenReturn(boardPage);

        //when
        Page<BoardDTO> resultPage = boardService.getAllBoard(pageable, userId, lastBoardId, sortType);

        //then
        //반환된 페이지가 비어있는지 확인
        assertTrue(resultPage.isEmpty());
    }

    @Test
    @DisplayName("요청한 게시물이 없을때 테스트")
    void getBoard_withNoBoard() {
        //given
        Long boardId = 1L;
        Long userId = 1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> boardService.getBoard(boardId, userId))
                .isInstanceOf(BoardNotFoundException.class).hasMessage(null);
    }

    @Test
    @DisplayName("첫 요청 태그 검색 테스트")
    void findBoardsByTags_withFirstRequest() {
        //given
        Pageable pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());
        Long userId = 1L;
        Long lastBoardId = 0L;
        String tags = "tag,tag2";

        List<Board> boardList = Arrays.asList(createBoard(1L), createBoard(2L));
        Page<Board> boardPage = new PageImpl<>(boardList);
        when(boardRepository.findBoardsByTagNames(Arrays.asList(tags.split(",")), pageable))
                .thenReturn(Optional.of(boardPage));

        //when
        boardService.findBoardsByTags(tags, userId, lastBoardId, pageable);

        //then
        //boardRepository.findBoardsByTagNames 호출하는 지 확인
        verify(boardRepository, times(1)).findBoardsByTagNames(anyList(), eq(pageable));
    }

    @Test
    @DisplayName("두 번째 요청 태그 검색 테스트")
    void findBoardsByTags_withSecondRequest() {
        //given
        Pageable pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());
        Long userId = 1L;
        Long lastBoardId = 10L;
        String tags = "tag,tag2";

        List<Board> boardList = Arrays.asList(createBoard(1L), createBoard(2L));
        Page<Board> boardPage = new PageImpl<>(boardList);
        when(boardRepository.findBoardsByTagNames(Arrays.asList(tags.split(",")), pageable))
                .thenReturn(Optional.of(boardPage));

        //when
        boardService.findBoardsByTags(tags, userId, lastBoardId, pageable);

        //then
        //boardRepository.findBoardsByTagNames 호출하는 지 확인
        verify(boardRepository, times(1)).findBoardsByTagNamesWithLastBoardId(anyList(), eq(lastBoardId), eq(pageable));
    }

    @Test
    @DisplayName("받은 태그가 하나도 없을 때 태그 검색 테스트")
    void findBoardsByTags_withNoTags() {
        //given
        Pageable pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());
        Long userId = 1L;
        Long lastBoardId = 10L;

        //when
        Page<BoardDTO> resultPage = boardService.findBoardsByTags("", userId, lastBoardId, pageable);

        //then
        assertTrue(resultPage.isEmpty());
    }
    // 조회 테스트 끝

    //저장 테스트
    @Test
    @DisplayName("저장 테스트")
    void saveBoard() {
        //given
        Long userId = 1L;
        BoardCreateRequestDTO requestDTO = createBoardCreateRequestDTO();
        List<MultipartFile> images = createDummyImageList();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        //when
        boardService.save(userId, requestDTO, images);

        //then
        verify(boardRepository, times(1)).save(any());
        verify(tagService, times(1)).save(any(), any());
        verify(imageService, times(1)).fileListWrite(any(), any());
    }

    @Test
    @DisplayName("이미지 첨부된게 하나도 없을 때 저장 테스트")
    void saveBoard_withNoImage() {
        //given
        Long userId = 1L;
        BoardCreateRequestDTO requestDTO = createBoardCreateRequestDTO();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        //when
        assertThatThrownBy(() -> boardService.save(userId, requestDTO, null))
                .isInstanceOf(NoImageException.class);

        //then
        verify(boardRepository, never()).save(any());
        verify(tagService, never()).save(any(), any());
        verify(imageService,never()).fileListWrite(any(),any());
    }

    //수정 테스트
    @Test
    @DisplayName("수정 테스트")
    void updateBoard(){
        //given
        Long boardId=1L;
        BoardUpdateRequestDTO requestDTO = createUpdateRequestDTO();
        List<MultipartFile> images = createDummyImageList();
        Board board= mock(Board.class);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        //when
        boardService.updateBoard(boardId, requestDTO, requestDTO.getImageUrls(), images);

        //then
        verify(board, times(1)).update(requestDTO.getContent());
        verify(tagRepository,times(1)).deleteByBoard_Id(boardId);
        verify(tagRepository,times(2)).save(any());
        verify(imageService,times(1)).updateFileList(any(),anyList(),anyList());
    }

    @Test
    @DisplayName("이미지 첨부된게 하나도 없을 때 수정 테스트")
    void updateBoard_withNoImage(){
        //given
        Long boardId=1L;
        BoardUpdateRequestDTO requestDTO = createUpdateRequestDTO();
        Board board= mock(Board.class);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        //when, then
        assertThatThrownBy(() -> boardService.updateBoard(boardId, requestDTO, null, null))
                .isInstanceOf(NoImageException.class);
    }

}