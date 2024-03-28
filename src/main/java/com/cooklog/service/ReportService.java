package com.cooklog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cooklog.dto.ReportedContentDTO;
import com.cooklog.model.User;
import com.cooklog.repository.BlacklistRepository;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final UserRepository userRepository;
	private final BoardRepository boardRepository;
	private final BlacklistRepository blacklistRepository;

	public List<ReportedContentDTO> findReportedContents() {
		List<ReportedContentDTO> reportedContents = new ArrayList<>();
		List<User> reportedUsers = userRepository.findByReportCountGreaterThan(0);

		for (User user : reportedUsers) {
			// 사용자가 블랙리스트에 있는지 여부를 확인
			boolean isBlacklisted = blacklistRepository.findOneByUserIdx(user.getIdx()).isPresent();

			reportedContents.add(new ReportedContentDTO(
				user.getNickname(),
				user.getReportCount(),
				user.getIdx(),
				isBlacklisted // 블랙리스트 여부를 DTO에 설정
			));
		}

		// 댓글 정보 조회 및 추가 (수정)
		// for (User user : reportedUsers) {
		// 	List<Comment> comments = commentRepository.findByUserIdx(user.getIdx());
		// 	comments.forEach(comment -> reportedContents.add(new ReportedContentDTO(
		// 		user.getNickname(),
		// 		"댓글: " + comment.getContent(),
		// 		user.getReportCount(),
		// 		user.getIdx()
		// 	)));
		// }

		return reportedContents;
	}
}