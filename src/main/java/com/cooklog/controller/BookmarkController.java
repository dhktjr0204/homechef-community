package com.cooklog.controller;

import com.cooklog.dto.CustomUserDetails;
import com.cooklog.service.BookmarkService;
import com.cooklog.service.CustomIUserDetailsService;
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
    private final CustomIUserDetailsService userDetailsService;

    @PostMapping("/add")
    public ResponseEntity<?> addMark(@RequestParam("boardId") Long boardId) {
        Long currentUserIdx = userDetailsService.getUserIdx();
        bookmarkService.addMark(currentUserIdx,boardId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<?> cancelMark(@RequestParam("boardId") Long boardId) {
        Long currentUserIdx = userDetailsService.getUserIdx();
        bookmarkService.cancelMark(currentUserIdx,boardId);

        return ResponseEntity.ok().build();
    }
}
