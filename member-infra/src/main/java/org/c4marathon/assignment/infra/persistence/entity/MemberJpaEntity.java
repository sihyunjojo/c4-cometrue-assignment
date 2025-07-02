package org.c4marathon.assignment.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.model.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "member")
public class MemberJpaEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String email;

	private String password;

	public static MemberJpaEntity fromDomain(Member member) {
		return MemberJpaEntity.builder()
			.id(member.getId())
			.name(member.getName())
			.email(member.getEmail())
			.password(member.getPassword())
			.build();
	}

	public Member toDomain() {
		return Member.of(id, name, email, password, super.getCreatedAt(), super.getUpdatedAt());
	}
}
