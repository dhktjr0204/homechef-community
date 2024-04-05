package com.cooklog.service;


import com.cooklog.dto.*;

import com.cooklog.exception.user.AlreadyExistsEmailException;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.Blacklist;
import com.cooklog.repository.BlacklistRepository;
import com.cooklog.repository.BoardRepository;

import com.cooklog.model.Bookmark;
import com.cooklog.repository.BookmarkRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cooklog.model.Role;
import com.cooklog.model.User;
import com.cooklog.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BCryptPasswordEncoder encoder;
    private final BlacklistRepository blacklistRepository;

    // JoinDTO 객체를 받아 사용자 정보를 추가(저장)하는 메서드
    @Override
    public void joinSave(JoinDTO joinDTO) {
        if (userRepository.existsByEmail(joinDTO.getEmail())) {
            throw new AlreadyExistsEmailException(); // 이메일이 존재하는 경우 예외 던짐
        }

        User user = User.builder()
                .nickname(joinDTO.getNickname())
                .email(joinDTO.getEmail())
                .password(encoder.encode(joinDTO.getPassword()))
                .build();

        userRepository.save(user);

    }

    @Override
    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            UserDTO dto = new UserDTO();
            dto.setIdx(user.getIdx());
            dto.setNickname(user.getNickname());
            dto.setEmail(user.getEmail());
            return dto;
        }
        return null;
    }

    // 모든 유저의 정보를 UserDTO 리스트로 변환하여 반환
    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> new UserDTO(user.getIdx(), user.getNickname(), user.getEmail(), user.getIntroduction(), user.getRole(), user.getReportCount(),user.isDeleted())
            ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setRole(role);
        userRepository.save(user);

        // 사용자의 역할이 BLACK으로 변경되는 경우에만 Black 테이블에 추가
        if (role == Role.BLACK) {
            Blacklist black = new Blacklist();
            black.setUser(user); // Black 엔터티에 User 설정
            blacklistRepository.save(black); // Black 엔터티 저장
        }
    }

    // 사용자 탈퇴 유무 업데이트 후 저장
    @Override
    public void updateUserDeleted(Long userIdx) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자는 존재하지 않습니다."));
        user.deleted(userIdx);

        userRepository.save(user);
    }

    // 사용자가 올린 글 갯수 반환
    @Override
    public Long getNumberOfBoardByUserId(Long userIdx) {

        User user = userRepository.findById(userIdx).orElseThrow(() -> new UsernameNotFoundException("해당 사용자는 존재하지 않습니다."));

        return boardRepository.countBoardByUserIdx(userIdx);
    }

    @Override
    public void resetReportCount(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setReportCount(0); // 신고 횟수를 0으로 초기화
            userRepository.save(user); // 변경된 사용자 정보 저장
        });
    }
}

