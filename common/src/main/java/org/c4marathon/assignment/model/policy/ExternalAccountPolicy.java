package org.c4marathon.assignment.model.policy;

import org.c4marathon.assignment.enums.AccountType;

import lombok.Getter;

@Getter
public enum ExternalAccountPolicy {
    COMPANY(-1L, "01-77-77777777", AccountType.MAIN_ACCOUNT),
    TEMPORARY_CHARGING(-2L, "00-00-00000000", AccountType.EXTERNAL_ACCOUNT);

    private final Long id;
    private final String number;
    private final AccountType type;

    ExternalAccountPolicy(Long id, String number, AccountType type) {
        this.id = id;
        this.number = number;
        this.type = type;
    }
}
