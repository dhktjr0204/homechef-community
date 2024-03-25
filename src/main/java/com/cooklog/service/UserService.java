package com.cooklog.service;

import com.cooklog.dto.UserDTO;

public interface UserService {
	UserDTO findUserById(Long id);
	//impl에서 만들어야할 메소드 정의
}
