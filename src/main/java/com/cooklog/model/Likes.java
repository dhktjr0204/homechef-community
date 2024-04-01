package com.cooklog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "likes",uniqueConstraints = {@UniqueConstraint(name = "user_board_unique",columnNames = {"user_idx,board_id"})})
public class Likes {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_idx")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id")
	private Board board;

	public Likes(User user,Board board) {
		this.user = user;
		this.board = board;
	}
}
