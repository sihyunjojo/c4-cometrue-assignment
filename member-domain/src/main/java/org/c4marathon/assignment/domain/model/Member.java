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

	public static Member of(String name, String email, String password) {
		return new Member(null, name, email, password, null, null);
	}

	public static Member of(Long id, String name, String email, String password) {
        return new Member(id, name, email, password, null, null);
    }

    public static Member of(Long id, String name, String email, String password, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Member(id, name, email, password, createdAt, updatedAt);
    }

}
