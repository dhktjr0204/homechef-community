package com.cooklog.service;

import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.BoardUpdateRequestDTO;
import com.cooklog.dto.CommentDTO;
import com.cooklog.model.Board;
import com.cooklog.model.Image;
import com.cooklog.model.Tag;
import com.cooklog.model.User;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.TagRepository;
import com.cooklog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final TagRepository tagRepository;

    @Override
    public Page<BoardDTO> getAllBoard(Pageable pageable, Long userId, Long lastBoardId) {
        Page<Board> boardPage;

        if (lastBoardId == 0) {
            boardPage= boardRepository.findAll(pageable);
        }else{
            boardPage=boardRepository.findAll(lastBoardId, pageable);
        }

        if(boardPage.isEmpty()){
            //예외처리
        }

        return boardPage.map(board -> convertBoardToDTO(board,userId));
    }

    @Override
    public BoardDTO getBoard(Long boardId, Long userId) {

        Board board = boardRepository.findById(boardId).orElseThrow();

        return convertBoardToDTO(board, userId);
    }

    private BoardDTO convertBoardToDTO(Board board, Long userId) {
        //게시물에 연결된 댓글들만 가져오기
        List<CommentDTO> commentList = board.getComments().stream()
                .filter(x -> x.getParentCommentId() == 0)
                .map(comment -> CommentDTO.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .userId(comment.getUser().getIdx())
                        .userName(comment.getUser().getNickname())
                        .boardId(comment.getBoard().getId())
                        .build()).toList();

        BoardDTO boardDTO = BoardDTO.builder()
                .id(board.getId())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .readCount(board.getReadCount())
                .userId(board.getUser().getIdx())
                .userNickname(board.getUser().getNickname())
                .profileImage(board.getUser().getProfileImage())
                .imageNames(board.getImages().stream().map(Image::getName).collect(Collectors.toList()))
                .tags(board.getTags().stream().map(Tag::getName).collect(Collectors.toList()))
                .comments(commentList)
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

