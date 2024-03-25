package com.cooklog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.repository.TagRepository;

@Service
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepository;

	@Autowired
	public TagServiceImpl(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	//구현 로직
}
