package com.cooklog.service;


import java.util.List;


import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.BoardUpdateRequestDTO;
import com.cooklog.model.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
	Page<BoardDTO> getAllBoard(Pageable pageable, Long userId, Long lastBoardId, String sortType);
	Board save(Long userId, String content);
	Board updateBoardAndTags(Long boardId, BoardUpdateRequestDTO boardUpdateRequestDTO);
	void updateReadCnt(Long boardId);
	void deleteBoard(Long boardId);
	BoardDTO getBoard(Long boardId, Long userId);

	List<BoardDTO> findAllBoards();

	Page<BoardDTO> getSearchByText(String keyword, Long userId, Pageable pageable);

	List<BoardDTO> findBoardsByUserId(Long userId);
}
