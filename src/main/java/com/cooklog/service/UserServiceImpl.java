package com.cooklog.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.cooklog.dto.UserDTO;
import com.cooklog.model.User;
import com.cooklog.repository.UserRepository;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  

    private final UserRepository userRepository;

    // 회원가입
    @Override
    public void join(@Valid UserDTO userDTO) {
        if (emailExists(userDTO.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다: " + userDTO.getEmail());
        }
        //비밀번호 유효성 검사
        isValidPassword(userDTO.getPassword());

        //이메일 유효성 검사
        if (!isValidEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("유효하지 않은 이메일 주소입니다: " + userDTO.getEmail());
        }

        User user = new User(userDTO.getNickname(), userDTO.getEmail(), userDTO.getPassword());
        userRepository.save(user);
    }

    // 이메일 중복 검사
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // id로 회원 찾기
    @Override
    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            UserDTO dto = new UserDTO();
            dto.setNickname(user.getNickname());
            dto.setEmail(user.getEmail());
            return dto;
        }
        return null;
    }

    // 비밀번호 유효성 검사
    @Override
    public boolean isValidPassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            throw new IllegalArgumentException("비밀번호는 영문자를 포함해야 합니다.");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("비밀번호는 숫자를 포함해야 합니다.");
        }
        if (!password.matches(".*[!@#$%^&*()].*")) {
            throw new IllegalArgumentException("비밀번호는 특수문자를 포함해야 합니다.");
        }
        return true;
    }

    // 이메일 유효성 검사
    public boolean isValidEmail(String email) {
        // 이메일 정규 표현식
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        // 패턴을 컴파일한 Pattern 객체 생성
        Pattern pattern = Pattern.compile(emailRegex);
        // 주어진 이메일이 패턴과 일치하는지 검사하여 반환
        return pattern.matcher(email).matches();
    }

    // 모든 유저의 정보를 UserDTO 리스트로 변환하여 반환
    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> new UserDTO(user.getIdx(), user.getNickname(), user.getEmail(), user.getRole(), user.getReportCount(),user.isDeleted())
            ).collect(Collectors.toList());
    }
}

