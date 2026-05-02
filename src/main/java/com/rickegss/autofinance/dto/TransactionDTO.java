package com.rickegss.autofinance.dto;

import com.rickegss.autofinance.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionDTO (

    String description,

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    BigDecimal amount,

    @NotNull(message = "O tipo é obrigatório")
    TransactionType type,

    @NotNull(message = "A categoria é obrigatória")
    String category,

    @NotNull(message = "A data é obrigatória")
    @PastOrPresent(message = "A data não pode ser futura")
    LocalDate date
) {}
