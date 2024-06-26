package com.cooklog.service;


import com.cooklog.dto.*;

import com.cooklog.exception.user.AlreadyExistsEmailException;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.Blacklist;
import com.cooklog.model.Comment;
import com.cooklog.repository.BlacklistRepository;
import com.cooklog.repository.BoardRepository;

import com.cooklog.model.Bookmark;
import com.cooklog.repository.BookmarkRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cooklog.model.Role;
import com.cooklog.model.User;
import com.cooklog.repository.UserRepository;


import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public
class UserServiceImpl implements UserService {

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
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            // User 엔티티를 UserDTO로 변환
            UserDTO userDTO = new UserDTO(
                user.getIdx(),
                user.getNickname(),
                user.getEmail(),
                user.getIntroduction(),
                user.getRole(),
                user.getReportCount(),
                user.isDeleted(),
                userRepository.countPostsByUserId(user.getIdx()), // 게시글 수
                userRepository.sumLikesByUserId(user.getIdx()) // 좋아요 수
            );
            userDTOs.add(userDTO);
        }

        return userDTOs;
    }
    //등급 상승 조건에 만족하는 사람들 체크.
    public void checkAndUpgradeUserRank(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        int postCount = userRepository.countPostsByUserId(user.getIdx());
        int likesCount = userRepository.sumLikesByUserId(user.getIdx());

        Role currentRole = user.getRole();
        boolean upgraded = false;

        if (currentRole.equals(Role.USER) && postCount >= 5) {
            currentRole = Role.USER2;
            upgraded = true;
        } else if (currentRole.equals(Role.USER2) && likesCount >= 10) {
            currentRole = Role.USER3;
            upgraded = true;
        }

        if (upgraded) {
            user.setRole(currentRole);
            userRepository.save(user);
        }
    }

    //등급 하락 조건에 맞는 사람들 체크
    public void checkAndDowngradeUserRank(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        int postCount = userRepository.countPostsByUserId(user.getIdx());
        int likesCount = userRepository.sumLikesByUserId(user.getIdx());

        Role currentRole = user.getRole();
        boolean downgraded = false;

        // 강등 조건
        if (currentRole.equals(Role.USER3) && (postCount < 5 || likesCount < 10)) {
            currentRole = Role.USER2;
            downgraded = true;
        } else if (currentRole.equals(Role.USER2) && postCount < 5) {
            currentRole = Role.USER;
            downgraded = true;
        }

        if (downgraded) {
            user.setRole(currentRole);
            userRepository.save(user);
        }
    }
    
    // 매시 정각에(서버 시간대 기준)에 실행되는 스케줄러 메서드
    @Scheduled(cron = "0 0 * * * ?")
    public void upgradeUserRoles() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            checkAndUpgradeUserRank(user.getIdx());
            checkAndDowngradeUserRank(user.getIdx());
        }
    }


    @Override
    @Transactional
    public void updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setRole(role);
        userRepository.save(user);

        // 이미 블랙리스트에 등록된 상태인지 확인
        boolean isAlreadyBlacklisted = blacklistRepository.existsByUser(user);
        if (role == Role.BLACK && !isAlreadyBlacklisted) {
            // 블랙리스트에 추가
            Blacklist blacklist = new Blacklist();
            blacklist.setUser(user);
            blacklistRepository.save(blacklist);
        } else if (role != Role.BLACK && isAlreadyBlacklisted) {
            // 블랙리스트에서 제거
            blacklistRepository.deleteByUser(user);
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

    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 실행(초,분,시,일,월,요일)
    @Transactional
    public void removeExpiredBlacklists() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Blacklist> expiredBlacklists = blacklistRepository.findByCreatedAtBefore(thirtyDaysAgo);

        for (Blacklist expiredBlacklist : expiredBlacklists) {
            User user = expiredBlacklist.getUser();
            user.setRole(Role.USER); // 사용자 역할을 USER로 업데이트
            user.setReportCount(0); // 신고 횟수를 0으로 초기화
            userRepository.save(user); // 역할 업데이트를 위해 사용자 정보 저장

            blacklistRepository.delete(expiredBlacklist);
        }
    }
    // @Scheduled(fixedRate = 60000) // 매 1분마다 실행 // 시연용 테스트 코드
    // public void removeExpiredBlacklistEntries() {
    //     List<Blacklist> blacklists = blacklistRepository.findAll();
    //     LocalDateTime now = LocalDateTime.now();
    //
    //     for (Blacklist blacklist : blacklists) {
    //         // 블랙리스트에 추가된 후 1분이 지났는지 확인
    //         if (Duration.between(blacklist.getCreatedAt(), now).toMinutes() >= 1) {
    //             User user = blacklist.getUser();
    //             user.setRole(Role.USER); // 사용자 역할을 USER로 업데이트
    //             user.setReportCount(0); // 신고 횟수를 0으로 초기화
    //             userRepository.save(user); // 역할 업데이트를 위해 사용자 정보 저장
    //
    //             blacklistRepository.delete(blacklist); // 블랙리스트에서 제거
    //             System.out.println("블랙리스트에서 사용자가 해제되었습니다: " + blacklist.getUser().getNickname());
    //         }
    //     }
    // }

    @Override
    public Page<UserDTO> searchUsers(String category, String term, Pageable pageable) {
        Page<User> users;
            if ("nickname".equals(category)) {
                users = userRepository.findByNicknameContaining(term, pageable);
            } else if ("email".equals(category)) {
                users = userRepository.findByEmailContaining(term, pageable);
            } else if ("role".equals(category)) {
                try {
                    Role role = Role.valueOf(term.toUpperCase()); // 여기서 예외가 발생할 수 있음
                    users = userRepository.findByRole(role, pageable);
                } catch (IllegalArgumentException e) {
                    return Page.empty(pageable);
                }
            }else {
                users = Page.empty(pageable);
            }
        return users.map(this::convertToUserDTO);
    }
    private UserDTO convertToUserDTO(User user) {
        return new UserDTO(user);
    }
}

