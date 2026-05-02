package com.rickegss.autofinance.service;

import com.rickegss.autofinance.dto.TransactionDTO;
import com.rickegss.autofinance.entity.Transaction;
import com.rickegss.autofinance.entity.TransactionType;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface  TransactionService {

    Transaction create(String userEmail, @Valid TransactionDTO dto);
    List<Transaction> findAllByUser(String userEmail);
    List<Transaction> findByUserAndMonth(String userEmail, int year, int month);
    BigDecimal sumByUserAndType(String userEmail, TransactionType type, LocalDate start, LocalDate end);
    void delete(Long transactionId, String userEmail);
}
