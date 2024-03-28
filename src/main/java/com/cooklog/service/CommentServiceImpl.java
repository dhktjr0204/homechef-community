package com.cooklog.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.dto.CommentDTO;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;

	@Override
	public List<CommentDTO> findAllComments() {
		return commentRepository.findAll().stream()
			.map(comment -> new CommentDTO(
				comment.getId(),
				comment.getContent(),
				comment.getCreatedAt(),
				comment.getUpdatedAt(),
				comment.getUser().getIdx(),
				comment.getUser().getNickname(),
				comment.getBoard().getId()
			)).collect(Collectors.toList());
	}


	@Override
	public void deleteComment(Long commentId) {
		commentRepository.deleteById(commentId);
	}

	// @Override
	// public List<CommentDTO> findCommentsByUserId(Long userId) {
	// 	return commentRepository.findByUserId(userId).stream()
	// 		.map(comment -> new CommentDTO(comment.getId(), comment.getContent(), comment.getUser().getNickname()))
	// 		.collect(Collectors.toList());
	// }
}
