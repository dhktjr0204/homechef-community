package com.cooklog.service;

import com.cooklog.dto.CustomUserDetails;
import com.cooklog.exception.board.BoardNotFoundException;
import com.cooklog.exception.bookmark.AlreadyBookmarkedException;
import com.cooklog.exception.bookmark.NotBookmarkedYetException;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.Board;
import com.cooklog.model.Bookmark;
import com.cooklog.model.User;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.BookmarkRepository;
import com.cooklog.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Transactional
    @Override
    public void addMark(Long userIdx, Long boardId) {
        User validUser = validateUser(userIdx);
        Board validBoard = validateBoard(boardId);

        Optional<Bookmark> bookmark =  bookmarkRepository.findByUserIdxAndBoardId(validUser.getIdx(),validBoard.getId());

        if(bookmark.isPresent()) {//이미 사용자가 해당 게시물을 북마크로 등록해놓았다면
            throw new AlreadyBookmarkedException();
        }

        Bookmark newBookmark = new Bookmark(validUser,validBoard);
        bookmarkRepository.save(newBookmark);
    }

    @Transactional
    @Override
    public void cancelMark(Long userIdx, Long boardId) {
        User validUser = validateUser(userIdx);
        Board validBoard = validateBoard(boardId);

        Optional<Bookmark> bookmark =  bookmarkRepository.findByUserIdxAndBoardId(validUser.getIdx(),validBoard.getId());

        if(bookmark.isEmpty()) {
            throw new NotBookmarkedYetException();
        }

        bookmarkRepository.delete(bookmark.get());
    }

    private User validateUser(Long userIdx) {
        return userRepository.findById(userIdx).orElseThrow(NotValidateUserException::new);
    }

    private Board validateBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
    }
}
