package com.cooklog.controller;

import com.cooklog.dto.BoardCreateRequestDTO;
import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.BoardUpdateRequestDTO;
import com.cooklog.dto.CommentDTO;
import com.cooklog.dto.CustomUserDetails;
import com.cooklog.model.Board;
import com.cooklog.model.Comment;
import com.cooklog.model.Tag;
import com.cooklog.service.BoardService;
import com.cooklog.service.CommentService;
import com.cooklog.service.ImageService;
import com.cooklog.service.TagService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	private final CommentService commentService;

	@GetMapping("/{id}")
	public String getBoard(@PathVariable Long id,Model model) throws FileNotFoundException {

		Long userId=1L;

		//조회수 업데이트
		boardService.updateReadCnt(id);

		BoardDTO board = boardService.getBoard(id, userId);

		//s3에서 가져온 file URL boardDTO에 넣기
		List<String> fileUrls = imageService.fileListLoad(board.getImageNames());

		board.setImageUrls(fileUrls);

		model.addAttribute("board", board);

		return "board/board";
	}

	@GetMapping("/write")
	public String getWriteForm(Model model){
		model.addAttribute("board", new BoardCreateRequestDTO());
		return "board/boardForm";
	}

	@PostMapping("/write")
	public ResponseEntity<?> save(BoardCreateRequestDTO boardCreateRequestDTO, @RequestPart("images")List<MultipartFile> images) throws IOException {
//		long userId=1;

		// 현재 인증된(로그인한) 사용자의 idx 가져옴
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long userId = userDetails.getIdx();

		Board board = boardService.save(userId, boardCreateRequestDTO.getContent());
		List<Tag> tags = tagService.save(boardCreateRequestDTO.getTags(), board);
		List<String> fileNames = imageService.fileListWrite(images,board);

		return ResponseEntity.ok("/board/"+board.getId());
	}

	@GetMapping("/edit/{id}")
	public String getEditForm(@PathVariable Long id, Model model) throws FileNotFoundException {

		Long userId=1L;

		BoardDTO board = boardService.getBoard(id, userId);

		//s3에서 가져온 file URL boardDTO에 넣기
		List<String> fileUrls= imageService.fileListLoad(board.getImageNames());
		board.setImageUrls(fileUrls);

		model.addAttribute("board", board);

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
	//댓글 추가
	@PostMapping("/{boardId}/comments")
	public ResponseEntity<CommentDTO> addComment(@PathVariable Long boardId, @RequestBody CommentDTO commentDTO) {
		CommentDTO savedComment = commentService.addComment(boardId, commentDTO);
		return ResponseEntity.ok(savedComment);
	}
	// 댓글 수정
	@PutMapping("/comments/{commentId}")
	public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO commentDTO) {
		CommentDTO updatedComment = commentService.updateComment(commentId, commentDTO);
		if (updatedComment != null) {
			return ResponseEntity.ok(updatedComment);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	//댓글 삭제
	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
		commentService.deleteComment(commentId);
		return ResponseEntity.ok().build();
	}
	@GetMapping("/{boardId}/comments")
	public ResponseEntity<?> getComments(
		@PathVariable Long boardId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "5") int limit) {

		Page<CommentDTO> commentPage = commentService.getCommentsByBoardId(boardId, page, limit);
		return ResponseEntity.ok(commentPage);
	}
	@GetMapping("/{parentId}/replies")
	public ResponseEntity<List<CommentDTO>> getRepliesByCommentId(
		@PathVariable Long parentId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "5") int size
	) {
		List<CommentDTO> replies = commentService.getRepliesByCommentId(parentId, page, size);
		return ResponseEntity.ok(replies);
	}
}

