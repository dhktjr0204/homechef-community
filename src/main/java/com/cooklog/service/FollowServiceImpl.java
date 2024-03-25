package com.cooklog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.repository.FollowRepository;

@Service
public class FollowServiceImpl implements FollowService {

	private final FollowRepository followRepository;

	@Autowired
	public FollowServiceImpl(FollowRepository followRepository) {
		this.followRepository = followRepository;
	}

	//구현 로직
}
