package com.cooklog.service;

import com.cooklog.exception.board.BoardNotFoundException;
import com.cooklog.exception.bookmark.AlreadyBookmarkedException;
import com.cooklog.exception.bookmark.NotBookmarkedYetException;
import com.cooklog.model.Board;
import com.cooklog.model.Bookmark;
import com.cooklog.model.User;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.BookmarkRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final BoardRepository boardRepository;

    //특정 게시물을 북마크로 등록해놓는 메서드
    @Transactional
    @Override
    public void addMark(User currentUser, Long boardId) {
        Board validBoard = validateBoard(boardId);

        Optional<Bookmark> bookmark =  bookmarkRepository.findByUserIdxAndBoardId(currentUser.getIdx(),validBoard.getId());

        if(bookmark.isPresent()) {//이미 사용자가 해당 게시물을 북마크로 등록해놓았다면
            throw new AlreadyBookmarkedException();
        }

        Bookmark newBookmark = new Bookmark(currentUser,validBoard);
        bookmarkRepository.save(newBookmark);
    }

    //특정 게시물을 북마크 해지하는 메서드
    @Transactional
    @Override
    public void cancelMark(User currentUser, Long boardId) {
        Board validBoard = validateBoard(boardId);

        Optional<Bookmark> bookmark =  bookmarkRepository.findByUserIdxAndBoardId(currentUser.getIdx(),validBoard.getId());

        if(bookmark.isEmpty()) {//이미 사용자가 북마크로 등록해놓지 않았다면
            throw new NotBookmarkedYetException();
        }

        bookmarkRepository.delete(bookmark.get());
    }

    private Board validateBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
    }
}
