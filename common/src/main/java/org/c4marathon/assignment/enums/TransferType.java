package org.c4marathon.assignment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

	@JsonValue
    public String getValue() {
        return name();
    }

    @JsonCreator
    public static TransferType fromValue(String value) {
        return valueOf(value);
    }
}
