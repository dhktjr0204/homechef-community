package com.cooklog.service;

import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CommentDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
	List<CommentDTO> getCommentInfoByBoardId(Page<BoardDTO> allBoard);
}
