package com.cooklog.service;

import com.cooklog.dto.*;
import com.cooklog.model.Bookmark;
import java.util.List;

import com.cooklog.model.Role;

import com.cooklog.model.User;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UserService {

	//회원 가입 정보 저장
	void joinSave(JoinDTO joinDTO);

	UserDTO findUserById(Long id);

	// 모든 유저의 정보를 가져오는 메소드
	List<UserDTO> findAllUsers();

	// 사용자의 역할을 업데이트하는 메소드 추가
	void updateUserRole(Long userId, Role role);

	// 사용자 탈퇴유무 업데이트 메소드
	void updateUserDeleted(Long userId);

	// 사용자가 올린 게시물 갯수 가져오는 메소드
	Long getNumberOfBoardByUserId(Long userIdx);

	void resetReportCount(Long userId);

}
