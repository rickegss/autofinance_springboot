package com.rickegss.autofinance.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FinancialGoal {
    BALANCED("balanced"),
    DEBT_RECOVERY("debt_recovery"),
    INVESTMENT("investment");

    private final String key;
}
