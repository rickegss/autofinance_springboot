package com.rickegss.autofinance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record WithdrawDTO (
    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero.")
    BigDecimal amount,

    LocalDate withdrawDate,

    String description   
    
){}
