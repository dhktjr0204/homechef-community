package com.cooklog.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cooklog.exception.likes.AlreadyLikedException;
import com.cooklog.exception.likes.NotLikedYetException;
import com.cooklog.model.Board;
import com.cooklog.model.Likes;
import com.cooklog.model.Role;
import com.cooklog.model.User;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.LikesRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

//단위테스트 작성 준비
@ExtendWith(MockitoExtension.class)
class LikesServiceImplTest {
    //가짜 객체 만들어서 반환해주는 어노테이션
    @Mock
    private LikesRepository likesRepository;

    @Mock
    private BoardRepository boardRepository;

    //@Mock 또는 @Spy로 생성된 가짜 객체를 자동으로 주입시켜주는 어노테이션
    @InjectMocks
    private LikesServiceImpl likesService;

    //Board 객체 생성을 위해 만드는 메서드
    private Board createBoard(Long boardId) {
        Board board = Board.builder()
            .id(boardId)
            .content("test")
            .createdAt(LocalDateTime.now())
            .readCount(1)
            .user(new User())
            .images(new ArrayList<>())
            .tags(new ArrayList<>())
            .likes(new ArrayList<>())
            .bookmarks(new ArrayList<>())
            .build();

        return board;
    }

    private User createUser(Long userIdx) {
        User user = new User(userIdx,"hanju","test@test","test","안녕하세요", Role.USER,"프로필이미지url",0,false,new ArrayList<>());
        return user;
    }

    @Test
    @DisplayName("특정 게시물의 총 좋아요 수를 반환")
    public void getNumberOfLikesByBoardId() {
        //given : boardId와 좋아요 기댓값 준비
        Long boardId = 1L;
        Long expectedResult = 5L;
        //boardId = 1인 Board 객체 생성
        Board board = createBoard(boardId);

        //mock 객체의 특정 메서드를 호출했을 때 반환값을 지정
        //boardRepository의 findById가 호출될 때, new Board()를 반환하도록 설정했다
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(likesRepository.countByBoardId(boardId)).thenReturn(expectedResult);

        //when
        Long actualResult = likesService.getNumberOfLikesByBoardId(boardId);

        //then
        assertEquals(expectedResult,actualResult);
        //모의 객체에 대해 특정 메서드가 호출됐는지 확인
        verify(boardRepository).findById(boardId);
        verify(likesRepository).countByBoardId(boardId);
    }

    @Test
    @DisplayName("좋아요 저장 테스트")
    public void addLikeTest() {
        //given
        User user = createUser(1L);
        Long boardId = 1L;
        Board board = createBoard(1L);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(likesRepository.findByUserIdxAndBoardId(user.getIdx(),board.getId())).thenReturn(Optional.empty());

        //when
        likesService.addLike(user,board.getId());

        //then
        verify(likesRepository).save(any(Likes.class));
    }

    @Test
    @DisplayName("좋아요 저장시 좋아요가 이미 눌러져 있을때 테스트")
    public void AlreadyLikedAddTest() {
        //given
        User user = createUser(1L);
        Long boardId = 1L;
        Board board = createBoard(boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(likesRepository.findByUserIdxAndBoardId(user.getIdx(),board.getId())).thenReturn(Optional.of(new Likes()));

        //when,then
        Throwable exception = assertThrows(AlreadyLikedException.class,() -> {
            likesService.addLike(user,board.getId());
        });

        assertEquals("해당 게시물은 이미 좋아요가 눌러져 있습니다.",exception.getMessage());
    }

    @Test
    @DisplayName("좋아요 취소 테스트")
    public void cancelLikeTest() {
        Long userIdx = 1L;
        User user = createUser(userIdx);
        Long boardId = 1L;
        Board board = createBoard(boardId);
        Likes like = new Likes(user,board);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(likesRepository.findByUserIdxAndBoardId(userIdx,boardId)).thenReturn(Optional.of(like));

        //when
        likesService.cancelLike(user,boardId);

        //then
        verify(likesRepository).delete(like);
    }

    @Test
    @DisplayName("좋아요 취소시 좋아요가 이미 취소되어있을때 테스트")
    public void NotLikedYetTest() {
        //given
        Long userIdx = 1L;
        User user = createUser(userIdx);
        Long boardId = 1L;
        Board board = createBoard(boardId);
        Likes like = new Likes(user,board);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(likesRepository.findByUserIdxAndBoardId(userIdx,boardId)).thenReturn(Optional.empty());

        //when,then
        Throwable exception = assertThrows(NotLikedYetException.class,() -> {
            likesService.cancelLike(user,board.getId());
        });

        assertEquals("해당 게시물은 이미 좋아요가 눌러져 있지 않습니다.",exception.getMessage());
    }

}