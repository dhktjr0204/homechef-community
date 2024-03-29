package com.cooklog.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cooklog.model.Blacklist;
import com.cooklog.model.User;
import com.cooklog.repository.BlacklistRepository;
import com.cooklog.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {
	private final BlacklistRepository blacklistRepository;
	private final UserRepository userRepository;


	@Override
	@Transactional
	public void addToBlacklist(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
		if (!blacklistRepository.existsByUserIdx(userId)) {
			Blacklist blacklist = new Blacklist();
			blacklist.setUser(user);
			blacklist.setCreatedAt(LocalDateTime.now());
			blacklistRepository.save(blacklist);
		}
	}

	@Override
	public void removeFromBlacklist(Long userId) {
		blacklistRepository.findOneByUserIdx(userId).ifPresent(blacklist -> {
			blacklistRepository.delete(blacklist);
		});
	}
}
