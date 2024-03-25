package com.cooklog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.repository.BoardRepository;

@Service
public class BoardServiceImpl implements BoardService {

	private final BoardRepository boardRepository;

	@Autowired
	public BoardServiceImpl(BoardRepository boardRepository) {
		this.boardRepository = boardRepository;
	}

	//구현 로직
}

