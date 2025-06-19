package org.c4marathon.assignment.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Member {

	private final Long id;
	private final String name;
	private final String email;
	private final String password;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;


	public static Member register(String name, String email, String password) {
		return new Member(null, name, email, password);
	}

	public static Member to(Long id, String name, String email, String password) {
		return new Member(id, name, email, password);
	}

	private Member(Long id, String name, String email, String password) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.createdAt = null;
		this.updatedAt = null;
	}

}
