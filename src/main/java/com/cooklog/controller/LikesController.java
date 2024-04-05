package com.cooklog.controller;

import com.cooklog.model.User;
import com.cooklog.service.CustomIUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	private final CustomIUserDetailsService userDetailsService;

	@PostMapping("/add")
	public ResponseEntity<?> addLike(@RequestParam("boardId") long boardId) {
		User currentUser = userDetailsService.isValidCurrentUser();
		likesService.addLike(currentUser, boardId);
		Long likesNum = likesService.getNumberOfLikesByBoardId(boardId);

		return ResponseEntity.ok(likesNum);
	}

	@DeleteMapping("/cancel")
	public ResponseEntity<?> cancelLike(@RequestParam("boardId") long boardId) {
		User currentUser = userDetailsService.isValidCurrentUser();
		likesService.cancelLike(currentUser,boardId);
		Long likesNum = likesService.getNumberOfLikesByBoardId(boardId);

		return ResponseEntity.ok(likesNum);
	}
}
