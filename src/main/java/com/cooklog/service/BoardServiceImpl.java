package com.cooklog.service;

import com.cooklog.model.Board;
import com.cooklog.model.User;
import com.cooklog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.repository.BoardRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Override
    public Board save(Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저아이디가 존재하지 않습니다."));

        Board board = Board.builder()
                .user(user)
                .content(content)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .readCount(0).build();

        return boardRepository.save(board);
    }


    //구현 로직
}

