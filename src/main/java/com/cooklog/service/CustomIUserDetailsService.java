package com.cooklog.service;

import com.cooklog.dto.CustomUserDetails;
import com.cooklog.dto.UserDTO;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.User;
import com.cooklog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티에서 사용자 정보를 가져오는 서비스 로직
@Service
@RequiredArgsConstructor
public class CustomIUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ImageService imageService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, DisabledException {
        User userData = userRepository.findByEmail(email);

        if(userData == null) {
            throw new UsernameNotFoundException(email);
        } else if (userData.isDeleted()) {
            // 탈퇴 회원 계정 비활성화
            throw new DisabledException(userData.getNickname());
        }

        // 시큐리티 세션에 유저 정보 저장
        return new CustomUserDetails(userData);
    }


    public User isValidCurrentUser() {
      
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return userRepository.findById(userDetails.getIdx()).orElseThrow(NotValidateUserException::new);
    }

    //현재 로그인한 유저의 idx를 가져오는 메서드 , 아해 getCurrentUserDTO와 중복되는 코드가 있어서 리팩토링 필요
    public Long getUserIdx() {
        User currentUser = isValidCurrentUser();

        return currentUser.getIdx();
    }


    public UserDTO getCurrentUserDTO() {
        User user = isValidCurrentUser();

        String profileUrl = imageService.fileLoad(user.getProfileImage());

        return UserDTO.builder()
                .idx(user.getIdx())
                .nickname(user.getNickname())
                .introduction(user.getIntroduction()==null ? "": user.getIntroduction())
                .profileImageName(user.getProfileImage())
                .profileImageUrl(profileUrl)
                .role(user.getRole())
                .isDeleted(user.isDeleted()).build();
    }
}
