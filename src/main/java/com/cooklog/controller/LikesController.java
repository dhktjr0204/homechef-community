package com.cooklog.controller;

import com.cooklog.dto.CustomUserDetails;
import com.cooklog.dto.LikesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cooklog.service.LikesService;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikesController {

	private final LikesService likesService;

	//게시물의 좋아요 개수뿐만 아니라 사용자가 게시물에 좋아요를 누른상태인지 아닌지 확인해야 하므로 getPostLikes 에서 getLikesInfo로 변경
//	@GetMapping("")
//	public ResponseEntity<?> getLikesInfo(@RequestParam("boardId") long boardId) {
//
//		boolean exists = likesService.existsByUserIdAndBoardId(getUserIdx(),boardId);
//		Long likesNum = likesService.getNumberOfLikesByBoardId(boardId);
//
//		//이부분이 걸리긴한다.. model로 isLike()를 확인하는데 굳이 exists를 확인할 필요가 있을까?
//		LikesDTO likesDTO = LikesDTO.builder()
//			.liked(exists)
//			.totalCount(likesNum)
//			.build();
//
//		return ResponseEntity.ok(likesDTO);
//	}

	@PostMapping("/add")
	public ResponseEntity<?> addLike(@RequestParam("boardId") long boardId) {

		likesService.addLike(getUserIdx(),boardId);
		Long likesNum = likesService.getNumberOfLikesByBoardId(boardId);

		return ResponseEntity.ok(likesNum);
	}

	@DeleteMapping("/cancel")
	public ResponseEntity<?> cancelLike(@RequestParam("boardId") long boardId) {

		likesService.cancelLike(getUserIdx(),boardId);
		Long likesNum = likesService.getNumberOfLikesByBoardId(boardId);

		return ResponseEntity.ok(likesNum);
	}

	//현재 인증된 사용자의 idx를 가져온다
	private Long getUserIdx() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//인증되지 않은 사용자면 자동으로 로그인 화면으로 리다이렉트 되게 스프링 시큐리티 설정이 되어 있다
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return userDetails.getIdx();
	}
}
