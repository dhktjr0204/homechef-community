package com.cooklog.dto;

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
	private String profileImage;
	private int reportCount;
	private boolean isDeleted;

	public UserDTO(Long idx, String nickname, String email, Role role, int reportCount, boolean isDeleted) {
		this.idx = idx;
		this.nickname = nickname;
		this.email = email;
		this.role = role;
		this.reportCount = reportCount;
		this.isDeleted = isDeleted;
	}

	public UserDTO() {

	}
}