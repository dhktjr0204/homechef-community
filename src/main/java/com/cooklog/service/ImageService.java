package com.cooklog.service;

import com.cooklog.dto.BoardDTO;
import com.cooklog.model.Board;
import com.cooklog.model.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ImageService {
	List<String> fileListWrite(List<MultipartFile> file, Board board);
    List<String> fileListLoad(List<String> fileNames);
    String fileWrite(MultipartFile file);
    String fileLoad(String fileName);
    void deleteS3(String fileName);
    void updateFileList(Board board, List<String> originalFiles, List<MultipartFile> newFiles);
}
