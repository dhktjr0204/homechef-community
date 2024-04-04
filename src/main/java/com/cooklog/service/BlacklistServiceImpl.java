package com.cooklog.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cooklog.model.Blacklist;
import com.cooklog.model.Role;
import com.cooklog.model.User;
import com.cooklog.repository.BlacklistRepository;
import com.cooklog.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {
	private final BlacklistRepository blacklistRepository;
	private final UserRepository userRepository;
	private final UserService userService;


	@Override
	@Transactional
	public void addToBlacklist(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
		if (!blacklistRepository.existsByUserIdx(userId)) {
			Blacklist blacklist = new Blacklist();
			blacklist.setUser(user);
			blacklist.setCreatedAt(LocalDateTime.now());
			blacklistRepository.save(blacklist);
			userService.updateUserRole(userId, Role.BLACK); // 역할을 블랙리스트로 변경
		}
	}

	@Override
	@Transactional
	public void removeFromBlacklist(Long userId) {
		blacklistRepository.findOneByUserIdx(userId).ifPresent(blacklist -> {
			blacklistRepository.delete(blacklist);
			userService.updateUserRole(userId, Role.USER); // 역할을 미식 초보로 변경

			// 신고 횟수를 초기화하는 코드 추가
			userService.resetReportCount(userId);
		});
	}
}
