package com.cooklog.service;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CommentDTO;
import com.cooklog.dto.LatestCommentWithTotalCountDTO;
import com.cooklog.model.Board;
import com.cooklog.model.Comment;

import jakarta.persistence.Version;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cooklog.dto.CommentDTO;
import com.cooklog.model.User;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.CommentRepository;
import com.cooklog.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;

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


	//댓글 삭제 메서드
	@Version
	@Transactional
	@Override
	public void deleteComment(Long commentId) {
		commentRepository.deleteById(commentId);
	}
	// 댓글 수정 메서드
	@Override
	public CommentDTO updateComment(Long commentId, CommentDTO commentDTO) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new RuntimeException("댓글을 찾지 못했습니다."));
		comment.setContent(commentDTO.getContent());
		Comment updatedComment = commentRepository.save(comment);
		return convertToDto(updatedComment);
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
	@Override
	public CommentDTO addComment(Long boardId, CommentDTO commentDTO) {
		System.out.println("Received comment data: " + commentDTO);
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new RuntimeException("Board not found"));
		User user = userRepository.findById(commentDTO.getUserId())
			.orElseThrow(() -> new RuntimeException("User not found"));
		// 대댓글인 경우 parentCommentId를 DTO에서 받아오고, 그렇지 않은 경우 0을 설정
		Long parentCommentId = commentDTO.getParentCommentId() != null ? commentDTO.getParentCommentId() : 0L;
		Comment newComment = new Comment();
		newComment.setBoard(board);
		newComment.setUser(user);
		newComment.setContent(commentDTO.getContent());
		newComment.setParentCommentId(parentCommentId);
		Comment savedComment = commentRepository.save(newComment);

		return new CommentDTO(
			savedComment.getId(),
			savedComment.getContent(),
			savedComment.getCreatedAt(),
			savedComment.getUser().getIdx(),
			savedComment.getUser().getNickname(),
			savedComment.getBoard().getId(),
			savedComment.getUser().getProfileImage()
		);
	}
	@Override
	public List<CommentDTO> findCommentsByBoardId(Long boardId) {
		List<Comment> comments = commentRepository.findByBoardId(boardId);
		return comments.stream()
			.map(this::convertToDto)
			.collect(Collectors.toList());
	}

	@Override
	public Page<CommentDTO> getCommentsByBoardId(Long boardId, int page, int limit) {
		Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
		Page<Comment> commentsPage = commentRepository.findByBoardId(boardId, pageable);
		return commentsPage.map(this::convertToDto); // Page 객체를 직접 반환
	}

	private CommentDTO convertToDto(Comment comment) {
		User user = comment.getUser();
		return new CommentDTO(
			comment.getId(),
			comment.getContent(),
			comment.getCreatedAt(),
			user.getIdx(),
			user.getNickname(),
			comment.getBoard().getId(),
			user.getProfileImage() // 이 부분은 User 엔티티에 프로필 이미지 필드가 있다고 가정
		);
	}

	@Override
	public List<CommentDTO> getRepliesByCommentId(Long parentId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Comment> replies = commentRepository.findByParentCommentId(parentId, pageable);
		return replies.stream()
			.map(this::convertToDto)
			.collect(Collectors.toList());
	}
}
