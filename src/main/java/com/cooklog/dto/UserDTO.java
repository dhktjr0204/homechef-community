package com.cooklog.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.cooklog.model.Role;

@Getter
@Setter
public class UserDTO {
	private Long idx;

    @NotBlank(message = "닉네임을 입력해주세요.")
	private String nickname;
	@Email(message = "유효한 이메일 형식이 아닙니다.")
	private String email;
	@NotBlank(message = "비밀번호를 입력해주세요.")
	private String password;
	private String introduction;
	private Role role;
	private String profileImageName;
	private String profileImageUrl;
	private int reportCount;
	private boolean isDeleted;
	private int postCount; // 사용자가 작성한 게시글 수
	private int likesCount; // 사용자가 받은 좋아요 수
//	private List<BoardDTO> myPageList;

	@Builder
	public UserDTO(Long idx, String nickname, String introduction, String profileImageName, String profileImageUrl, Role role, boolean isDeleted) {
		this.idx = idx;
		this.nickname = nickname;
		this.introduction = introduction;
		this.profileImageName = profileImageName;
		this.profileImageUrl=profileImageUrl;
		this.role = role;
		this.isDeleted = isDeleted;
	}


	public UserDTO() {

	}

	public UserDTO(Long idx, String nickname, String email, String introduction, Role role, int reportCount, boolean deleted, int postCount, int likesCount) {
		this.idx = idx;
		this.nickname = nickname;
		this.email = email;
		this.introduction = introduction;
		this.role = role;
		this.reportCount = reportCount;
		this.isDeleted = deleted;
		this.postCount = postCount;
		this.likesCount = likesCount;
	}
}