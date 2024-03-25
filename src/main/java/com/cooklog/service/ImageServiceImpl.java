package com.cooklog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.repository.ImageRepository;

@Service
public class ImageServiceImpl implements ImageService {

	private final ImageRepository imageRepository;

	@Autowired
	public ImageServiceImpl(ImageRepository imageRepository) {
		this.imageRepository = imageRepository;
	}

	//구현 로직
}
