package com.cooklog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.repository.LikesRepository;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {

	private final LikesRepository likesRepository;


	//구현 로직
}
