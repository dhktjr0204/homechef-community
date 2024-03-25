package com.cooklog.dto;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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


//	private String introduction; //마이페이지
//	private String role; //마이페이지
//	private String profileImage; //마이페이지
//	private Boolean isDeleted; //마이페이지

}