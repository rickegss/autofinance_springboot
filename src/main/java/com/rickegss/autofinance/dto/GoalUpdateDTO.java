package com.rickegss.autofinance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record GoalUpdateDTO(
    @Size(min = 1, max = 100, message = "Nome deve ter entre 1 e 100 caracteres")
    String name,

    @DecimalMin(value = "0.01", message = "Valor alvo deve ser maior que zero")
    BigDecimal targetAmount
) {}
