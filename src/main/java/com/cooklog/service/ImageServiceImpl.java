package com.cooklog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.repository.ImageRepository;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

	private final ImageRepository imageRepository;

	//구현 로직
}
