package com.cooklog.service;

import com.cooklog.dto.*;
import com.cooklog.model.Bookmark;
import java.util.List;

import com.cooklog.model.Role;

import com.cooklog.model.User;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface UserService {

	//회원 가입 정보 저장
	void joinSave(JoinDTO joinDTO);

//	void join(@Valid UserDTO userDTO);

//	//이메일 중복 검색
//	boolean emailExists(String email);

	UserDTO findUserById(Long id);

//	//비밀번호 유효성 검사
//	boolean isValidPassword(String password);

	// 모든 유저의 정보를 가져오는 메소드
	List<UserDTO> findAllUsers();

	// 사용자의 역할을 업데이트하는 메소드 추가
	void updateUserRole(Long userId, Role role);

	// 사용자 탈퇴유무 업데이트 메소드
	void updateUserDeleted(Long userId);

	// 사용자가 올린 게시물 갯수 가져오는 메소드
	Long getNumberOfBoardByUserId(Long userIdx);

	// 사용자의 북마크 리스트를 가져오는 메소드
	List<BoardDTO> getBookmarkBoards(Long userIdx);

	// 사용자가 작성한 게시물 리스트를 가져오는 메소드
	List<MyPageDTO> getBoardByUserId(Long userIdx);

	// 사용자 프로필 이미지 URL 생성 메소드
	UserDTO getUserDTO(Long userIdx);

	// 로그인 한 사용자의 팔로우 팔로워 수 가져오는 메소드
	MyPageFollowCountDTO getFollowCountDTO(Long userIdx, Long loginUserId);

	void resetReportCount(Long userId);
}
