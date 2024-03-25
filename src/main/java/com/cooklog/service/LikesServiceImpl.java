package com.cooklog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.repository.LikesRepository;

@Service
public class LikesServiceImpl implements LikesService {

	private final LikesRepository likesRepository;

	@Autowired
	public LikesServiceImpl(LikesRepository likesRepository) {
		this.likesRepository = likesRepository;
	}

	//구현 로직
}
