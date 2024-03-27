package com.cooklog.service;

import com.cooklog.dto.BoardDTOInterface;
import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.BoardUpdateRequestDTO;
import com.cooklog.model.Board;
import com.cooklog.model.Image;
import com.cooklog.model.Tag;
import com.cooklog.model.User;
import com.cooklog.repository.TagRepository;
import com.cooklog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.cooklog.repository.BoardRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final TagRepository tagRepository;

    @Override
    public BoardDTO getBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        //양방향 이용했을때
        BoardDTO boardDTO = BoardDTO.builder()
                .id(board.getId())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .readCount(board.getReadCount())
                .userId(board.getUser().getIdx())
                .userNickname(board.getUser().getNickname())
                .profileImage(board.getUser().getProfileImage())
                .imageUrls(board.getImages().stream().map(Image::getName).collect(Collectors.toList()))
                .tags(board.getTags().stream().map(Tag::getName).collect(Collectors.toList()))
                .likeCount(board.getLikes().size())
                .isLike(board.getLikes().stream().anyMatch(like -> like.getUser().getIdx().equals(userId))).build();

        return boardDTO;

    }

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

    @Override
    public BoardDTOInterface findByBoardId(Long boardId, Long userId) {

        BoardDTOInterface boardDTOInterface = boardRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 boardId가 없습니다."));

        return boardDTOInterface;
    }

    @Transactional
    @Override
    public Board updateBoardAndTags(Long boardId, BoardUpdateRequestDTO boardDTO) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 boardId가 없습니다."));

        board.update(boardDTO.getContent(), LocalDateTime.now());

        //기존 태그 모두 삭제
        tagRepository.deleteByBoard_Id(boardId);

        //새 태그 저장
        if (boardDTO.getTags() != null) {
            for (String tag : boardDTO.getTags()) {
                tagRepository.save(Tag.builder().board(board).name(tag).build());
            }
        }

        return board;

    }

    @Transactional
    @Override
    public void updateReadCnt(Long boardId) {

        boardRepository.updateReadCnt(boardId);

    }

    @Transactional
    @Override
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 boardId가 없습니다."));
        boardRepository.delete(board);
    }
}

