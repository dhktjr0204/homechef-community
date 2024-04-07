package com.cooklog.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
		// 예시로 11번 ID 사용자 정보를 조회. 나중에 다시 만들때 운영자부터 만들어 1번 한개만 사용.
		UserDTO userDto = userService.findUserById(11L);

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
		// userId를 사용하여 비즈니스 로직 수행
		UserDTO userDto = userService.findUserById(userId);
		if (userDto != null) {
			model.addAttribute("user", userDto);
		} else {
			// 사용자이 없는 경우
		}
		return "manager/role-manager";
	}

	@PostMapping("/role-manager/update")
	public String updateUserRole(@RequestParam("userIdx") Long userId, @RequestParam("role") String roleString) {
		Role role = Role.valueOf(roleString); // 문자열을 Enum으로 변환
		userService.updateUserRole(userId, role);
		return "redirect:/manager/user"; // 업데이트 후에는 역할 관리 페이지로 리다이렉트
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
		// 통일성을 위해 reportedInfo를 사용
		model.addAttribute("reportedInfo", reportedContentsPage.getContent());
		model.addAttribute("currentPage", reportedContentsPage.getNumber() + 1);
		model.addAttribute("totalPages", reportedContentsPage.getTotalPages());
		return "manager/report-manager";
	}

	@PostMapping("/blacklist/add/{userId}")
	public ResponseEntity<?> addToBlacklist(@PathVariable Long userId) {
		blacklistService.addToBlacklist(userId);
		boolean isBlacklisted = true; // 실제 로직에 따라 결정됩니다.
		return ResponseEntity.ok(Map.of("message", "유저를 블랙리스트에 추가하였습니다.", "isBlacklisted", isBlacklisted));
	}

	@PostMapping("/blacklist/remove/{userId}")
	public ResponseEntity<?> removeFromBlacklist(@PathVariable Long userId) {
		blacklistService.removeFromBlacklist(userId);
		boolean isBlacklisted = false; // 실제 로직에 따라 결정됩니다.
		return ResponseEntity.ok(Map.of("message", "유저를 블랙리스트에서 해제하였습니다.", "isBlacklisted", isBlacklisted));
	}

	@GetMapping("/userPosts/{userId}")
	public String showUserPosts(@PathVariable Long userId, Model model) {
		// boards와 comments를 결합하여 UserPostDTO의 리스트를 생성하는 로직 구현
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

	// 신고된 유저 검색 기능
	@GetMapping("/report-management/search")
	public String searchReportedUsers(@RequestParam("nickname") String nickname,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "5") int size, // 기본 페이지 크기를 5로 설정
		Model model) {
		Pageable pageable = PageRequest.of(page, size);
		Page<ReportedContentDTO> reportedUsers = reportService.searchReported(nickname, pageable);
		model.addAttribute("reportedInfo", reportedUsers.getContent());
		model.addAttribute("currentPage", reportedUsers.getNumber() + 1);
		model.addAttribute("totalPages", reportedUsers.getTotalPages());
		model.addAttribute("nickname", nickname); // 검색어를 모델에 추가

		// 콘솔 로깅
		System.out.println("Searching reported users for nickname: " + nickname);
		System.out.println("Page: " + pageable.getPageNumber() + ", Size: " + pageable.getPageSize());
		System.out.println("Total pages: " + reportedUsers.getTotalPages() + ", Current page: " + (reportedUsers.getNumber() + 1));

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
