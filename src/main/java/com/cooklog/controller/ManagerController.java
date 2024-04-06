package com.cooklog.controller;

import java.util.List;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		// 예시로 1번 ID 사용자 정보를 조회
		UserDTO userDto = userService.findUserById(1L);

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
	public String showReportedContents(Model model) {
		List<ReportedContentDTO> reportedInfo = reportService.findReportedContents();
		model.addAttribute("reportedInfo", reportedInfo);
		return "manager/report-manager";
	}
	@PostMapping("/blacklist/add/{userId}")
	public ResponseEntity<String> addToBlacklist(@PathVariable Long userId) {
		try {
			blacklistService.addToBlacklist(userId);
			return ResponseEntity.ok("유저를 블랙리스트에 추가하였습니다.");
		} catch (Exception e) {
			// 에러 처리
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("블랙리스트 추가에 실패하였습니다: " + e.getMessage());
		}
	}

	@PostMapping("/blacklist/remove/{userId}")
	public ResponseEntity<String> removeFromBlacklist(@PathVariable Long userId) {
		try {
			blacklistService.removeFromBlacklist(userId);
			return ResponseEntity.ok("유저를 블랙리스트에서 해제하였습니다.");
		} catch (Exception e) {
			// 에러 처리
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("블랙리스트 해제에 실패하였습니다: " + e.getMessage());
		}
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
}
