package com.cooklog.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

	private String nickname;
	private String email;
	private String password;
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
	private List<Board> boards = new ArrayList<>();

//	@OneToMany(mappedBy = "user") 양방향
//	private List<Bookmark> bookmarks = new ArrayList<>();

	@Builder
	public User (String nickname, String email, String password, Role role, String introduction, String profileImage){
		this.nickname = nickname;
		this.email = email;
		this.password = password;
		this.role = role;
		this.introduction = introduction;
		this.profileImage = profileImage;
	}

	public void update(String nickname, String introduction) {
		this.nickname = nickname;
		this.introduction = introduction;
	}

	public void update(String profileImage){
		this.profileImage=profileImage;
	}
}
