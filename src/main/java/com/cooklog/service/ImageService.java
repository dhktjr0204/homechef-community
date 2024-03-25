package com.cooklog.service;

import com.cooklog.model.Board;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ImageService {
	List<String> fileWrite(List<MultipartFile> file, Board board) throws IOException;
    byte[] fileLoad(String fileName) throws FileNotFoundException;
}
