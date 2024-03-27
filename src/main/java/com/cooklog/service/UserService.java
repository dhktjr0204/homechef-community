package com.cooklog.service;

import com.cooklog.dto.JoinDTO;
import com.cooklog.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated
public interface UserService {

	//회원 가입
	public void join(JoinDTO joinDTO);
	UserDTO findUserById(Long id);
}
