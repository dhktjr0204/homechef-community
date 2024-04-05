package com.cooklog.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cooklog.exception.bookmark.AlreadyBookmarkedException;
import com.cooklog.exception.bookmark.NotBookmarkedYetException;
import com.cooklog.model.Board;
import com.cooklog.model.Bookmark;
import com.cooklog.model.Role;
import com.cooklog.model.User;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.BookmarkRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceImplTest {

    @InjectMocks
    private BookmarkServiceImpl bookmarkService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private BoardRepository boardRepository;

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
    @DisplayName("북마크 저장 테스트")
    public void addMarkTest() {
        //given
        Long userIdx = 1L;
        User user = createUser(userIdx);
        Long boardId = 1L;
        Board board = createBoard(boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(bookmarkRepository.findByUserIdxAndBoardId(user.getIdx(),boardId)).thenReturn(Optional.empty());

        //when
        bookmarkService.addMark(user,boardId);

        //then
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    @DisplayName("북마크 등록시 이미 북마크로 등록되어있을때 테스트")
    public void AlreadyBookmarkedTest() {
        //given
        Long userIdx = 1L;
        User user = createUser(userIdx);
        Long boardId = 1L;
        Board board = createBoard(boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(bookmarkRepository.findByUserIdxAndBoardId(user.getIdx(),boardId)).thenReturn(Optional.of(new Bookmark()));

        //when,then
        Throwable exception = assertThrows(AlreadyBookmarkedException.class,() -> {
            bookmarkService.addMark(user,boardId);
        });

        assertEquals("해당 게시물은 이미 북마크로 등록이 되어있습니다.",exception.getMessage());
    }

    @Test
    @DisplayName("북마크 취소 테스트")
    public void cancelBookmarkTest() {
        //given
        Long userIdx = 1L;
        User user = createUser(userIdx);
        Long boardId = 1L;
        Board board = createBoard(boardId);
        Bookmark bookmark = new Bookmark(user,board);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(bookmarkRepository.findByUserIdxAndBoardId(user.getIdx(),boardId)).thenReturn(Optional.of(bookmark));

        //when
        bookmarkService.cancelMark(user,boardId);

        //then
        verify(bookmarkRepository).delete(bookmark);
    }

    @Test
    @DisplayName("북마크를 취소했을시 이미 북마크로 등록되어있지 않은 게시물일때 테스트")
    public void NotBookmarkedYetTest() {
        //given
        Long userIdx = 1L;
        User user = createUser(userIdx);
        Long boardId = 1L;
        Board board = createBoard(boardId);
        Bookmark bookmark = new Bookmark(user,board);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(bookmarkRepository.findByUserIdxAndBoardId(user.getIdx(),boardId)).thenReturn(Optional.empty());

        //when,then
        Throwable exception = assertThrows(NotBookmarkedYetException.class,() -> {
            bookmarkService.cancelMark(user,boardId);
        });

        assertEquals("해당 게시물은 이미 북마크로 등록이 되어있지 않습니다.",exception.getMessage());
    }
}