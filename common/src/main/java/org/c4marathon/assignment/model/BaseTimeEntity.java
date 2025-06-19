package org.c4marathon.assignment.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // ✅ Auditing 기능 활성화
public abstract class BaseTimeEntity {

	@CreatedDate // ✅ Spring Data JPA에서 자동으로 설정
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate // ✅ @PreUpdate 없이 자동으로 변경됨
	private LocalDateTime updatedAt;
}
