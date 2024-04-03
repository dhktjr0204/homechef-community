package com.cooklog.service;

import com.cooklog.dto.MyPageUpdateRequestDTO;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.Image;
import com.cooklog.model.User;
import com.cooklog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService{
    private final ImageService imageService;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void updateUserProfile(Long userId,
                                  MyPageUpdateRequestDTO request,
                                  MultipartFile newImageFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(NotValidateUserException::new);

        if(newImageFile!=null){
            String saveS3FileName=null;
            try {
                saveS3FileName=imageService.fileWrite(newImageFile,userId);
            } catch (IOException e) {
                saveS3FileName="images/db181dbe-7139-4f6c-912f-a53f12de6789_기본프로필.png";
            }
            user.update(request.getNickname(), request.getIntroduction(),saveS3FileName);
        }else{
            user.update(request.getNickname(), request.getIntroduction(), request.getOriginalImage());
        }
    }
}
