package com.cooklog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
	private Long idx;
	private String nickname;
	private String email;
	private String password;
	private String introduction;
	private String role;
	private String profileImage;
	private Boolean isDeleted;

}