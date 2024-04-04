package com.cooklog.service;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


import com.cooklog.dto.BoardCreateRequestDTO;
import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.BoardUpdateRequestDTO;
import com.cooklog.model.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface BoardService {
    Page<BoardDTO> getAllBoard(Pageable pageable, Long userId, Long lastBoardId, String sortType);
    Page<BoardDTO> getAllBoardWithFollow(Pageable pageable, Long userId, Long lastBoardId);

    Board save(Long userId, BoardCreateRequestDTO requestDTO, List<MultipartFile> images);

    Board updateBoard(Long boardId, BoardUpdateRequestDTO boardUpdateRequestDTO, List<String> originalFiles, List<MultipartFile> newFiles);

    void updateReadCnt(Long boardId);

    void deleteBoard(Long boardId);

    BoardDTO getBoard(Long boardId, Long userId);

    List<BoardDTO> findAllBoards();

    Page<BoardDTO> getSearchByText(String keyword, Long userId, Long lastBoardId, Pageable pageable);

    Page<BoardDTO> findBoardsByTags(String tags, Long userId, Long lastBoardId, Pageable pageable);

    List<BoardDTO> findBoardsByUserId(Long userId);
}
