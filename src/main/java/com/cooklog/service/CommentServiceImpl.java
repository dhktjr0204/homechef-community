package com.cooklog.service;


import java.util.List;
import java.util.stream.Collectors;


import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CommentDTO;
import com.cooklog.dto.LatestCommentWithTotalCountDTO;
import com.cooklog.model.Comment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.cooklog.dto.CommentDTO;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.CommentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
				comment.getUser().getIdx(),
				comment.getUser().getNickname(),
				comment.getBoard().getId()
			)).collect(Collectors.toList());
	}


	@Override
	public void deleteComment(Long commentId) {
		commentRepository.deleteById(commentId);
	}

	public List<CommentDTO> getCommentInfoByBoardId(Page<BoardDTO> allBoard) {
		List<CommentDTO> commentDTOS= new ArrayList<>();

		for(BoardDTO board: allBoard){
			LatestCommentWithTotalCountDTO commentInfo = commentRepository
					.findLatestCommentByBoardId(board.getId(), 0L)
					.orElse(null);
			if(commentInfo!=null) {
				CommentDTO comment = CommentDTO.builder()
						.id(commentInfo.getId())
						.boardId(commentInfo.getId())
						.userId(commentInfo.getUserId())
						.userName(commentInfo.getUserName())
						.content(commentInfo.getContent())
						.createdAt(commentInfo.getCreatedAt())
						.contentCount(commentInfo.getTotalCount()).build();
				commentDTOS.add(comment);
			}else{
				commentDTOS.add(null);
			}
		}

		return commentDTOS;
	}


	@Override
	public List<CommentDTO> findCommentsByUserId(Long userId) {
		return commentRepository.findByUserIdx(userId).stream()
			.map(comment -> new CommentDTO(comment.getId(), comment.getContent(), comment.getCreatedAt()))
			.collect(Collectors.toList());
	}
}
