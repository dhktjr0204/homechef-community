package com.cooklog.controller;

import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.BoardCreateRequestDTO;
import com.cooklog.dto.BoardUpdateRequestDTO;
import com.cooklog.model.Board;
import com.cooklog.model.Tag;
import com.cooklog.service.BoardService;
import com.cooklog.service.ImageService;
import com.cooklog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;
	private final TagService tagService;
	private final ImageService imageService;

	@GetMapping("/{id}")
	public String getBoard(@PathVariable Long id,Model model) throws FileNotFoundException {

		Long userId=1L;

		//조회수 업데이트
		boardService.updateReadCnt(id);

		BoardDTO boardDTO = boardService.findByBoardId(id, userId);

		List<String> files = imageService.fileListLoad(boardDTO.getImageUrls());

		List<String> tags=tagService.getTags(boardDTO.getTags());

		model.addAttribute("board", boardDTO);
		model.addAttribute("images", files);
		model.addAttribute("tags", tags);

		return "board/board";
	}

	@GetMapping("/write")
	public String getWriteForm(Model model){
		model.addAttribute("board", new BoardCreateRequestDTO());
		return "board/boardForm";
	}

	@PostMapping("/write")
	public ResponseEntity<?> save(BoardCreateRequestDTO boardCreateRequestDTO, @RequestPart("images")List<MultipartFile> images) throws IOException {
		long userId=1;

		Board board = boardService.save(userId, boardCreateRequestDTO.getContent());
		List<Tag> tags = tagService.save(boardCreateRequestDTO.getTags(), board);
		List<String> fileNames = imageService.fileListWrite(images,board);

		return ResponseEntity.ok("/board/"+board.getId());
	}

	@GetMapping("/edit/{id}")
	public String getEditForm(@PathVariable Long id, Model model) throws FileNotFoundException {

		Long userId=1L;

		BoardDTO boardDTO = boardService.findByBoardId(id, userId);

		//파일명
		List<String> fileNames=imageService.getFileNameList(boardDTO.getImageUrls());

		//파일 url
		List<String> files = imageService.fileListLoad(boardDTO.getImageUrls());

		List<String> tags=tagService.getTags(boardDTO.getTags());

		model.addAttribute("board", boardDTO);
		model.addAttribute("images", files);
		model.addAttribute("imageNames", fileNames);
		model.addAttribute("tags", tags);

		return "board/boardEditForm";
	}

	@PutMapping("/edit/{id}")
	public ResponseEntity<?> edit(@PathVariable Long id, BoardUpdateRequestDTO boardUpdateRequestDTO,
					   @RequestPart(value = "images", required = false)List<MultipartFile> images) throws IOException {

		Board board = boardService.updateBoardAndTags(id, boardUpdateRequestDTO);
		imageService.updateFileList(board, boardUpdateRequestDTO.getImageUrls() ,images);

		return ResponseEntity.ok("/board/"+id);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id){
		boardService.deleteBoard(id);
		return ResponseEntity.ok("/");
	}
}
