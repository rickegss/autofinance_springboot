package com.rickegss.autofinance.dto;

import com.rickegss.autofinance.entity.FinancialGoal;

import java.math.BigDecimal;

public record ConfigDetailsDTO(FinancialGoal goal, BigDecimal income) {
}
