package com.cooklog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.repository.BlacklistRepository;

@Service
public class BlacklistServiceImpl implements BlacklistService {

	private final BlacklistRepository blacklistRepository;

	@Autowired
	public BlacklistServiceImpl(BlacklistRepository blacklistRepository) {
		this.blacklistRepository = blacklistRepository;
	}

	//구현 로직
}
