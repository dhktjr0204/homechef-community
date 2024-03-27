package com.cooklog.service;

import com.cooklog.model.Board;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ImageService {
	List<String> fileListWrite(List<MultipartFile> file, Board board) throws IOException;
    List<String> fileListLoad(List<String> fileNames) throws FileNotFoundException;
    List<String> getFileNameList(String images);
    void updateFileList(Board board, List<String> originalFiles, List<MultipartFile> newFiles) throws IOException;
}
