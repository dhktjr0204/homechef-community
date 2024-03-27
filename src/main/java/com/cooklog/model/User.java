package com.cooklog.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idx;
	@Column
	private String nickname;
	@Column
	private String email;
	@Column
	private String password;
	@Column
	private String introduction;

	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Role role;


	@Column(name = "profile_image")
	private String profileImage;

	@Column(name = "report_count")
	private int reportCount;

	@Column(name = "is_deleted", columnDefinition = "TINYINT(4)")
	private boolean isDeleted;

	@OneToMany(mappedBy = "user")
	private Set<Board> boards = new HashSet<>();

//	@Builder
//	public User (String nickname, String email, String password){
//		this.nickname = nickname;
//		this.email = email;
//		this.password = password;
//	}
}
