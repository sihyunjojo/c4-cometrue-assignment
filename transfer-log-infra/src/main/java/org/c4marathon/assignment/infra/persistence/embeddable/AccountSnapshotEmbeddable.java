package org.c4marathon.assignment.infra.persistence.embeddable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.c4marathon.assignment.domain.model.vo.AccountSnapshot;
import org.c4marathon.assignment.enums.AccountType;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AccountSnapshotEmbeddable {
	private Long id;

	@Enumerated(EnumType.STRING)
	private AccountType type;

	private String number;

	public AccountSnapshot toDomain() {
		return new AccountSnapshot(id, type, number);
	}

	public static AccountSnapshotEmbeddable fromDomain(AccountSnapshot snapshot) {
		return new AccountSnapshotEmbeddable(
			snapshot.id(),
			snapshot.type(),
			snapshot.number()
		);
	}
}
