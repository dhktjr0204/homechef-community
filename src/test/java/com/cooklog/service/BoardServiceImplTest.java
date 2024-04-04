package com.cooklog.service;

import com.cooklog.dto.BoardDTO;
import com.cooklog.exception.board.BoardNotFoundException;
import com.cooklog.model.Board;
import com.cooklog.model.Image;
import com.cooklog.model.User;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.TagRepository;
import com.cooklog.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    private Board creaeBoard(Long boardId){
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

    @Test
    @DisplayName("메인 페이지 첫 요청 테스트")
    void getAllBoard_withFirstRequest(){
        //given
        Pageable pageable= PageRequest.of(0,3, Sort.by("createdAt").descending());
        Long userId=1L;
        Long lastBoardId=0L;
        String sortType="cratedAt: DESC";

        List<Board> boardList = Arrays.asList(creaeBoard(1L), creaeBoard(2L));
        Page<Board> boardPage = new PageImpl<>(boardList);
        when(boardRepository.findAll(pageable)).thenReturn(boardPage);

        //converBoard때 필요한 것
        when(imageService.fileLoad(any())).thenReturn("");
        when(imageService.fileListLoad(anyList())).thenReturn(new ArrayList<>());

        //when
        boardService.getAllBoard(pageable, userId, lastBoardId, sortType);

        //then
        verify(boardRepository,times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("메인 페이지 조회순 테스트")
    void getAllBoard_withSortByReadCnt(){
        //given
        Pageable pageable= PageRequest.of(0,3, Sort.by("readCount").descending());
        Long userId=1L;
        Long lastBoardId=10L;
        String sortType="readCount: DESC";

        List<Board> boardList = Arrays.asList(creaeBoard(1L), creaeBoard(2L));
        Page<Board> boardPage = new PageImpl<>(boardList);
        when(boardRepository.findAllOrderByReadCount(lastBoardId,pageable)).thenReturn(boardPage);

        //converBoard때 필요한 것
        when(imageService.fileLoad(any())).thenReturn("");
        when(imageService.fileListLoad(anyList())).thenReturn(new ArrayList<>());

        //when
        boardService.getAllBoard(pageable, userId, lastBoardId, sortType);

        //then
        //조회순 로직이 1번 쓰였는지 확인
        verify(boardRepository,times(1)).findAllOrderByReadCount(lastBoardId,pageable);
    }

    @Test
    @DisplayName("메인페이지에 등록된 게시물이 없을 때 테스트")
    void getAllBoard_withNoBoard(){
        //given
        Pageable pageable= PageRequest.of(0,3, Sort.by("createdAt").descending());
        Long userId=1L;
        Long lastBoardId=0L;
        String sortType="createdAt: DESC";

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
    void getBoard_withNoBoard(){
        //given
        Long boardId=1L;
        Long userId=1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(()->boardService.getBoard(boardId, userId))
                .isInstanceOf(BoardNotFoundException.class).hasMessage(null);
    }

    @Test
    @DisplayName("받은 태그가 하나도 없을 때 태그 검색 테스트")
    void findBoardsByTags_withNoTags(){
        //given
        Pageable pageable= PageRequest.of(0,3, Sort.by("createdAt").descending());
        Long userId=1L;
        Long lastBoardId=10L;

        //when
        Page<BoardDTO> resultPage = boardService.findBoardsByTags("", userId,lastBoardId, pageable);

        //then
        assertTrue(resultPage.isEmpty());
    }
}