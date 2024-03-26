package com.cooklog.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.cooklog.model.Board;
import com.cooklog.model.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cooklog.repository.ImageRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
	private final ImageRepository imageRepository;
	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucketName}")
	private String bucket;

	@Override
	public List<String> fileWrite(List<MultipartFile> files,Board board) throws IOException {
		List<String> fileNameList=new ArrayList<>();

		for(int i=0; i<files.size(); i++){
			UUID uuid = UUID.randomUUID();

			String directory = "images/";

			String fileName = directory + uuid + "_" + files.get(i).getOriginalFilename();

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(files.get(i).getSize());
			metadata.setContentType(files.get(i).getContentType());

			amazonS3.putObject(bucket, fileName, files.get(i).getInputStream(), metadata);

			fileNameList.add(fileName);

			saveImageInDB(board, fileName, i+1);
		}
		return fileNameList;
	}

	@Override
	public byte[] fileLoad(String fileName) throws FileNotFoundException {
		return new byte[0];
	}

	private void saveImageInDB(Board board, String fileName, int order){
		Image image = Image.builder()
				.board(board)
				.name(fileName)
				.order(order).build();

		imageRepository.save(image);
	}
}
