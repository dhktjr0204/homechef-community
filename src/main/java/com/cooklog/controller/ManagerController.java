package com.cooklog.controller;

import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CommentDTO;
import com.cooklog.dto.ReportedContentDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.dto.UserPostDTO;
import com.cooklog.model.Role;
import com.cooklog.service.BlacklistService;
import com.cooklog.service.BoardService;
import com.cooklog.service.CommentService;
import com.cooklog.service.PostCombinationService;
import com.cooklog.service.ReportService;
import com.cooklog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerController {

	private final UserService userService;
	private final BoardService boardService;
	private final CommentService commentService;
	private final ReportService reportService;
	private final BlacklistService blacklistService;
	private final PostCombinationService postCombinationService;

	@GetMapping("/main")
	public String userProfile(Model model) {
		UserDTO userDto = userService.findUserById(11L); // 예시 ID 사용

		if (userDto != null) {
			model.addAttribute("user", userDto);
		}
		return "manager/manager";
	}

	@GetMapping("/user")
	public String listUsers(Model model) {
		List<UserDTO> users = userService.findAllUsers();
		model.addAttribute("users", users);
		return "manager/user-manager";
	}

	@GetMapping("/role-manager/{userIdx}")
	public String roleupdate(@PathVariable("userIdx") Long userId, Model model) {
		UserDTO userDto = userService.findUserById(userId);

		if (userDto != null) {
			model.addAttribute("user", userDto);
		}
		return "manager/role-manager";
	}

	@PostMapping("/role-manager/update")
	public String updateUserRole(@RequestParam("userIdx") Long userId, @RequestParam("role") String roleString) {
		Role role = Role.valueOf(roleString); // 문자열을 Enum으로 변환
		userService.updateUserRole(userId, role);
		return "redirect:/manager/user"; // 업데이트 후에는 등급 관리 페이지로 리다이렉트
	}

	@GetMapping("/board")
	public String listBoards(Model model) {
		List<BoardDTO> boards = boardService.findAllBoards();
		model.addAttribute("boards", boards);
		return "manager/board-manager";
	}

	@DeleteMapping("/board/delete/{id}")
	public ResponseEntity<?> deleteBoard(@PathVariable Long id) {
		try {
			boardService.deleteBoard(id); // 게시글 삭제 서비스 호출
			return ResponseEntity.ok().build(); // 성공 응답
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 중 오류 발생");
		}
	}

	@GetMapping("/comment")
	public String listComments(Model model) {
		List<CommentDTO> comments = commentService.findAllComments();
		model.addAttribute("comments", comments);
		return "manager/comment-manager";
	}

	@DeleteMapping("/comment/delete/{id}")
	public ResponseEntity<?> deleteComment(@PathVariable Long id) {
		commentService.deleteComment(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/report")
	public String showReportedContents(@PageableDefault(size = 5) Pageable pageable, Model model) {
		Page<ReportedContentDTO> reportedContentsPage = reportService.findReportedContentsPageable(pageable);
		model.addAttribute("reportedInfo", reportedContentsPage.getContent());
		model.addAttribute("currentPage", reportedContentsPage.getNumber() + 1);
		model.addAttribute("totalPages", reportedContentsPage.getTotalPages());
		return "manager/report-manager";
	}

	@PostMapping("/blacklist/add/{userId}")
	public ResponseEntity<?> addToBlacklist(@PathVariable Long userId) {
		blacklistService.addToBlacklist(userId);
		boolean isBlacklisted = true;
		return ResponseEntity.ok(Map.of("message", "유저를 블랙리스트에 추가하였습니다.", "isBlacklisted", isBlacklisted));
	}

	@PostMapping("/blacklist/remove/{userId}")
	public ResponseEntity<?> removeFromBlacklist(@PathVariable Long userId) {
		blacklistService.removeFromBlacklist(userId);
		boolean isBlacklisted = false;
		return ResponseEntity.ok(Map.of("message", "유저를 블랙리스트에서 해제하였습니다.", "isBlacklisted", isBlacklisted));
	}

	@GetMapping("/userPosts/{userId}")
	public String showUserPosts(@PathVariable Long userId, Model model) {
		List<UserPostDTO> posts = postCombinationService.combineBoardsAndComments(userId);
		model.addAttribute("posts", posts);
		return "manager/userPosts-manager";
	}

	@PostMapping("/comment/{commentId}")
	public ResponseEntity<?> reportComment(@PathVariable Long commentId) {
		reportService.reportComment(commentId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/boards/search")
	public ResponseEntity<Page<BoardDTO>> searchBoards(
		@RequestParam(value = "category", required = false, defaultValue = "content") String category,
		@RequestParam(value = "term", required = false, defaultValue = "") String term,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<BoardDTO> boards = boardService.searchBoards(category, term, pageable);
		return ResponseEntity.ok(boards);
	}

	@GetMapping("/comments/search")
	public ResponseEntity<Page<CommentDTO>> searchComments(
		@RequestParam(value = "category", required = false, defaultValue = "content") String category,
		@RequestParam(value = "term", required = false, defaultValue = "") String term,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<CommentDTO> comments = commentService.searchComments(category, term, pageable);
		return ResponseEntity.ok(comments);
	}

	@GetMapping("/reports/search")
	public String searchReportedUsers(@RequestParam("nickname") String nickname,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "5") int size, Model model) {
		Pageable pageable = PageRequest.of(page, size);
		Page<ReportedContentDTO> reportedUsers = reportService.searchReported(nickname, pageable);
		model.addAttribute("reportedInfo", reportedUsers.getContent());
		model.addAttribute("currentPage", reportedUsers.getNumber() + 1);
		model.addAttribute("totalPages", reportedUsers.getTotalPages());
		model.addAttribute("nickname", nickname);

		return "manager/report-manager";
	}

	@GetMapping("/users/search")
	public ResponseEntity<Page<UserDTO>> searchUsers(
		@RequestParam(value = "category", required = false, defaultValue = "nickname") String category,
		@RequestParam(value = "term", required = false, defaultValue = "") String term,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("role").descending());
		Page<UserDTO> users = userService.searchUsers(category, term, pageable);
		return ResponseEntity.ok(users);
	}
}
