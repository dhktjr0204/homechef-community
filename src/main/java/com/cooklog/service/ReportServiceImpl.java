package com.cooklog.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cooklog.dto.ReportedContentDTO;
import com.cooklog.model.Blacklist;
import com.cooklog.model.Board;
import com.cooklog.model.Comment;
import com.cooklog.model.Role;
import com.cooklog.model.User;
import com.cooklog.repository.BlacklistRepository;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.CommentRepository;
import com.cooklog.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
	private final UserRepository userRepository;
	private final BlacklistRepository blacklistRepository;
	private final CommentRepository commentRepository;
	private final BoardRepository boardRepository;


	//4번이상 신고 당할시 신고 유저 관리 페이지에 표시.
	public Page<ReportedContentDTO> findReportedContentsPageable(Pageable pageable) {
		int reportThreshold = 4; // 신고 횟수 기준
		Page<User> reportedUsersPage = userRepository.findByReportCountGreaterThanEqual(reportThreshold, pageable);

		// 조회된 User 엔티티를 ReportedContentDTO로 변환
		Page<ReportedContentDTO> reportedContentsPage = reportedUsersPage.map(user -> {
			boolean isBlacklisted = blacklistRepository.findOneByUserIdx(user.getIdx()).isPresent();
			return new ReportedContentDTO(
				user.getNickname(),
				user.getReportCount(),
				user.getIdx(),
				isBlacklisted
			);
		});

		return reportedContentsPage;
	}

	//댓글 신고 기능
	@Override
	public void reportComment(Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

		User user = userRepository.findById(comment.getUser().getIdx())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		user.increaseReportCount();
		userRepository.save(user);
	}
	//게시글 신고 기능
	@Override
	public void reportBoard(Long boardId) {
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

		User user = userRepository.findById(board.getUser().getIdx())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		user.increaseReportCount();
		userRepository.save(user);
	}

	@Override
	public Page<ReportedContentDTO> searchReported(String term, Pageable pageable) {
		return userRepository.findByNicknameContainingAndReportCountGreaterThanEqual(term, 4, pageable)
			.map(this::convertToReportedContentDTO);
	}
	private ReportedContentDTO convertToReportedContentDTO(User user) {
		boolean isBlacklisted = blacklistRepository.existsByUserIdx(user.getIdx());
		return new ReportedContentDTO(
			user.getNickname(),
			user.getReportCount(),
			user.getIdx(),
			isBlacklisted
		);
	}
}
