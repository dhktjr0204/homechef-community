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
	List<String> fileListWrite(List<MultipartFile> file, Board board) throws IOException;
    List<String> fileListLoad(List<String> fileNames) throws FileNotFoundException;
    String fileWrite(MultipartFile file, Long userId) throws IOException;
    String fileLoad(String fileName) throws FileNotFoundException;
    void updateFileList(Board board, List<String> originalFiles, List<MultipartFile> newFiles) throws IOException;
}
