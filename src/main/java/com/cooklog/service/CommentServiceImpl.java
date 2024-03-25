package com.cooklog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final BoardRepository boardRepository;

	@Autowired
	public CommentServiceImpl(CommentRepository commentRepository, BoardRepository boardRepository) {
		this.commentRepository = commentRepository;
		this.boardRepository = boardRepository;
	}

	//구현 로직
}
