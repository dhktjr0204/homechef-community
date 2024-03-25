package com.cooklog.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
	private String role;

	@Column(name = "profile_image")
	private String profileImage;

	@Column(name = "is_deleted", columnDefinition = "TINYINT(4)")
	private boolean isDeleted;

	@OneToMany(mappedBy = "user")
	private Set<Board> boards = new HashSet<>();

}
