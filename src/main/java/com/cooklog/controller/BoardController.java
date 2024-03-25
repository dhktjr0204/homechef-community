package com.cooklog.controller;

import com.cooklog.dto.BoardRequestDTO;
import com.cooklog.model.Board;
import com.cooklog.model.Tag;
import com.cooklog.service.BoardService;
import com.cooklog.service.ImageService;
import com.cooklog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;
	private final TagService tagService;
	private final ImageService imageService;

	@GetMapping("/write")
	public String getWriteForm(Model model){
		model.addAttribute("board", new BoardRequestDTO());
		return "board/boardForm";
	}

	@PostMapping("/write")
	public ResponseEntity<?> save(BoardRequestDTO boardRequestDTO, @RequestPart("images")List<MultipartFile> images) throws IOException {
		long userId=1;

		Board board = boardService.save(userId, boardRequestDTO.getContent());
		List<Tag> tags = tagService.save(boardRequestDTO.getTags(), board);
		List<String> fileNames = imageService.fileWrite(images,board);

		return ResponseEntity.ok("확인");
	}
}
