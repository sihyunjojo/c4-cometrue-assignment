package org.c4marathon.assignment.infra.persistence.embeddable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.c4marathon.assignment.domain.model.vo.SavingAccountSnapshot;
import org.c4marathon.assignment.enums.AccountType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SavingAccountSnapshotEmbeddable {

	private Long id;

	@Enumerated(EnumType.STRING)
	private AccountType type;

	private String number;

	public static SavingAccountSnapshotEmbeddable fromDomain(
		org.c4marathon.assignment.domain.model.vo.SavingAccountSnapshot snapshot) {
		return new SavingAccountSnapshotEmbeddable(snapshot.id(), snapshot.type(), snapshot.number());
	}

	public SavingAccountSnapshot toDomain() {
		return new SavingAccountSnapshot(id, type, number);
	}
}
