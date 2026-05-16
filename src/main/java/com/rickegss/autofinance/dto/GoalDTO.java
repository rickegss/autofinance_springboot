package com.rickegss.autofinance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record GoalDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Target amount is required")
        @DecimalMin(value = "0.01", message = "Target amount must be greater than zero")
        BigDecimal targetAmount,

        @NotNull(message = "Current amount is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Current amount cannot be negative")
        BigDecimal currentAmount

) {}
