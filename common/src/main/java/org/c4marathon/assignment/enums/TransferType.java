package org.c4marathon.assignment.enums;

public enum TransferType {
	IMMEDIATE,
	PENDING,
	CHARGE,
	FIXED_TERM,
	INTEREST;

	public boolean isSenderTimeAuto(TransferStatus status) {
		return switch (this) {
			case IMMEDIATE, CHARGE, FIXED_TERM, INTEREST -> true;
			case PENDING -> status == TransferStatus.PENDING;
		};
	}

	public boolean isReceiverTimeAuto(TransferStatus status) {
		return switch (this) {
			case IMMEDIATE, CHARGE, FIXED_TERM, INTEREST -> true;
			case PENDING -> status == TransferStatus.COMPLETED;
		};
	}
}
