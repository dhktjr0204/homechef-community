package com.cooklog.service;

import com.cooklog.dto.MyPageUpdateRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface MyPageService {
    void updateUserProfile(Long userId, MyPageUpdateRequestDTO myPageUpdateRequestDTO, MultipartFile newImageFile);
}
