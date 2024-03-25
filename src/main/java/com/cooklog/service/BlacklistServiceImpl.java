package com.cooklog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.repository.BlacklistRepository;

@Service
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {

	private final BlacklistRepository blacklistRepository;


	//구현 로직
}
