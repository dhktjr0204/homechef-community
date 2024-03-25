package com.cooklog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final BoardRepository boardRepository;


	//구현 로직
}
