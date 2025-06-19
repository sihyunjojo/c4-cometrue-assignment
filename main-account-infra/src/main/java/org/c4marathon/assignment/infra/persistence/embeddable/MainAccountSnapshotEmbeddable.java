package org.c4marathon.assignment.infra.persistence.embeddable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import org.c4marathon.assignment.domain.model.vo.MainAccountSnapshot;
import org.c4marathon.assignment.enums.AccountType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MainAccountSnapshotEmbeddable {

	private Long id;

	@Enumerated(EnumType.STRING)
	private AccountType type;

	private String number;

	public static MainAccountSnapshotEmbeddable fromDomain(MainAccountSnapshot snapshot) {
		return new MainAccountSnapshotEmbeddable(snapshot.id(), snapshot.type(), snapshot.number());
	}

	public MainAccountSnapshot toDomain() {
		return new MainAccountSnapshot(id, type, number);
	}
}
