package com.rickegss.autofinance.dto;

import com.rickegss.autofinance.entity.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record CycleDataDTO(
        LocalDate cycleStart,
        LocalDate cycleEnd,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal savings,
        BigDecimal savingsPercentage,
        BigDecimal expensesPercentage,
        Map<String, BigDecimal> expensesByCategory,
        List<Transaction> transactions
) {}