package com.cooklog.service;

import com.cooklog.dto.BoardDTOInterface;
import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.BoardUpdateRequestDTO;
import com.cooklog.model.Board;

public interface BoardService {
	Board save(Long userId, String content);
	BoardDTOInterface findByBoardId(Long boardId, Long userId);
	Board updateBoardAndTags(Long boardId, BoardUpdateRequestDTO boardUpdateRequestDTO);
	void updateReadCnt(Long boardId);
	void deleteBoard(Long boardId);
	BoardDTO getBoard(Long boardId, Long userId);
}
