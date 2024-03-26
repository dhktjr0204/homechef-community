package com.cooklog.service;

import com.cooklog.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated
public interface UserService {

	//회원 가입
	void join(@Valid UserDTO userDTO);

	//이메일 중복 검색
	boolean emailExists(String email);

	UserDTO findUserById(Long id);

	//비밀번호 유효성 검사
	boolean isValidPassword(String password);
}
