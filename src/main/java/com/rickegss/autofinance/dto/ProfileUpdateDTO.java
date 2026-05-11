package com.rickegss.autofinance.dto;


import com.rickegss.autofinance.entity.FinancialGoal;

import java.math.BigDecimal;

public record ProfileUpdateDTO (
    String name,
    String email,
    FinancialGoal financialGoal,
    BigDecimal monthlyIncome
) {}
