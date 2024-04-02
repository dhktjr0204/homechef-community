package com.cooklog.controller;

import com.cooklog.dto.CustomUserDetails;
import com.cooklog.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/add")
    public ResponseEntity<?> addMark(@RequestParam("boardId") Long boardId) {
        bookmarkService.addMark(getUserIdx(),boardId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<?> cancelMark(@RequestParam("boardId") Long boardId) {
        bookmarkService.cancelMark(getUserIdx(),boardId);

        return ResponseEntity.ok().build();
    }

    private Long getUserIdx() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getIdx();
    }
}
