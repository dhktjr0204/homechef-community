package com.cooklog.service;


import com.cooklog.dto.*;

import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.repository.BoardRepository;

import com.cooklog.model.Bookmark;
import com.cooklog.repository.BookmarkRepository;

import com.cooklog.repository.FollowRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cooklog.model.Board;
import com.cooklog.model.Role;
import com.cooklog.model.User;
import com.cooklog.repository.UserRepository;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ImageService imageService;
    private final FollowRepository followRepository;
    private final BCryptPasswordEncoder encoder;

    // JoinDTO 객체를 받아 사용자 정보를 추가(저장)하는 메서드
    @Override
    public void joinSave(JoinDTO joinDTO) {
        if (userRepository.existsByEmail(joinDTO.getEmail())) {
            return; // 이미 가입된 이메일인 경우 메서드 종료
        }

        User user = User.builder()
                .nickname(joinDTO.getNickname())
                .email(joinDTO.getEmail())
                .password(encoder.encode(joinDTO.getPassword()))
                .build();

        userRepository.save(user);
    }

    // 회원가입 유효성 검사
//        @Override
//        public void join(@Valid UserDTO userDTO) {
//            if (userRepository.existsByEmail(userDTO.getEmail())) {
//                throw new IllegalArgumentException("이미 존재하는 회원입니다: " + userDTO.getEmail());
//            }
//            //비밀번호 유효성 검사
//            isValidPassword(userDTO.getPassword());
//
//            //이메일 유효성 검사
//            if (!isValidEmail(userDTO.getEmail())) {
//                throw new IllegalArgumentException("유효하지 않은 이메일 주소입니다: " + userDTO.getEmail());
//            }
//
//            User user = new User(userDTO.getNickname(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getRole());
//            userRepository.save(user);
//        }
//
//       // 이메일 중복 검사
//        public boolean emailExists(String email) {
//            return userRepository.findByEmail(email).isPresent();
//        }
//
//        // 비밀번호 유효성 검사
//        @Override
//        public boolean isValidPassword(String password) {
//            if (password.length() < 8) {
//                throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
//            }
//            if (!password.matches(".*[a-zA-Z].*")) {
//                throw new IllegalArgumentException("비밀번호는 영문자를 포함해야 합니다.");
//            }
//            if (!password.matches(".*\\d.*")) {
//                throw new IllegalArgumentException("비밀번호는 숫자를 포함해야 합니다.");
//            }
//            if (!password.matches(".*[!@#$%^&*()].*")) {
//                throw new IllegalArgumentException("비밀번호는 특수문자를 포함해야 합니다.");
//            }
//            return true;
//        }
//
//        // 이메일 유효성 검사
//        public boolean isValidEmail(String email) {
//            // 이메일 정규 표현식
//            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
//            // 패턴을 컴파일한 Pattern 객체 생성
//            Pattern pattern = Pattern.compile(emailRegex);
//            // 주어진 이메일이 패턴과 일치하는지 검사하여 반환
//            return pattern.matcher(email).matches();
//        }

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
    public void updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("유저를 찾지 못함"));
        user.setRole(role);
        userRepository.save(user);
    }

    // 사용자 탈퇴 유무 업데이트 후 저장
    @Override
    public void updateUserDeleted(Long userIdx) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 userId가 없습니다."));
        user.deleted(userIdx);

        userRepository.save(user);
    }

    // 사용자가 올린 글 갯수 반환
    @Override
    public Long getNumberOfBoardByUserId(Long userIdx) {

        User user = userRepository.findById(userIdx).orElseThrow(() -> new IllegalArgumentException("해당 게시글은 존재하지 않는 게시글입니다."));

        return boardRepository.countBoardByUserIdx(userIdx);
    }

    @Override
    public List<BoardDTO> getBookmarkBoards(Long userIdx) {
        User user = userRepository.findById(userIdx)
            .orElseThrow(() -> new IllegalArgumentException("해당 userId가 없습니다."));

        List<Bookmark> bookmarkList = bookmarkRepository.findAllByUserIdx(userIdx);
        List<BoardDTO> boardList = bookmarkList.stream().map(bookmark -> new BoardDTO(bookmark.getBoard(),userIdx)).collect(Collectors.toList());

        return boardList;
    }

    // 사용자가 작성한 게시물의 대표 이미지 URL을 가져옴
    @Override
    public List<MyPageDTO> getBoardByUserId(Long userIdx) {
        List<MyPageDTO> myPageDTOList = new ArrayList<>();
        List<Board> boardList = boardRepository.findByUserIdx(userIdx);

        for (Board board : boardList ) {

            String boardImageUrl = null;
            try {
                boardImageUrl = imageService.fileLoad(board.getImages().get(0).getName());
            } catch (FileNotFoundException e) {
                boardImageUrl = "";
            }
            MyPageDTO myPageDTO = MyPageDTO.builder()
                    .id(board.getId())
                    .imageUrl(boardImageUrl).build();

            myPageDTOList.add(myPageDTO);
        }

        return myPageDTOList;
    }

    // 사용자 프로필 이미지 URL 생성 후 UserDTO 객체에 담는 메서드
    @Override
    public UserDTO getUserDTO(Long userIdx) {
        User user = userRepository.findById(userIdx).orElseThrow(NotValidateUserException::new);

        String profileImageUrl = null;
        try {
            profileImageUrl = imageService.fileLoad(user.getProfileImage());
        } catch (FileNotFoundException e) {
            profileImageUrl = "";
        }
        UserDTO userDTO = UserDTO.builder()
                .idx(user.getIdx())
                .nickname(user.getNickname())
                .introduction(user.getIntroduction())
                .profileImageUrl(profileImageUrl).build();

        return userDTO;
    }

    // 로그인 한 사용자의 팔로우-팔로워 수를 가져옴
    @Override
    public MyPageFollowCountDTO getFollowCountDTO(Long userIdx, Long loginUserId) {
        return followRepository.findFollowCountByUserId(userIdx, loginUserId);
    }

}

