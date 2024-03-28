package com.cooklog.service;

import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.BoardUpdateRequestDTO;
import com.cooklog.model.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
	Page<BoardDTO> getAllBoard(Pageable pageable, Long userId, Long lastBoardId);
	Board save(Long userId, String content);
	Board updateBoardAndTags(Long boardId, BoardUpdateRequestDTO boardUpdateRequestDTO);
	void updateReadCnt(Long boardId);
	void deleteBoard(Long boardId);
	BoardDTO getBoard(Long boardId, Long userId);
}
