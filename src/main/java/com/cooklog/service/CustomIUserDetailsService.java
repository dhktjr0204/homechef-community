package com.cooklog.service;

import com.cooklog.dto.CustomUserDetails;
import com.cooklog.dto.UserDTO;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.User;
import com.cooklog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

// 시큐리티에서 사용자 정보를 가져오는 서비스 로직
@Service
@RequiredArgsConstructor
public class CustomIUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ImageService imageService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException(email));

        User userData = userRepository.findByEmail(email);

        if(userData == null) {
            throw new UsernameNotFoundException(email);
        }
        return new CustomUserDetails(userData);
    }

    public UserDTO getCurrentUserDTO() throws FileNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        long userId = userDetails.getIdx();

        User user = userRepository.findById(userId).orElseThrow(NotValidateUserException::new);
        String profileUrl = imageService.fileLoad(user.getProfileImage());

        return UserDTO.builder()
                .idx(user.getIdx())
                .nickname(user.getNickname())
                .introduction(user.getIntroduction())
                .profileImageName(user.getProfileImage())
                .profileImageUrl(profileUrl)
                .role(user.getRole())
                .isDeleted(user.isDeleted()).build();
    }
}
