package com.rickegss.autofinance.controller;

import com.rickegss.autofinance.dto.TransactionDTO;
import com.rickegss.autofinance.entity.Transaction;
import com.rickegss.autofinance.entity.TransactionType;
import com.rickegss.autofinance.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> create(
            @Valid @RequestBody
            TransactionDTO dto,
            Principal principal) {
        Transaction created = transactionService.create(principal.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> findAll(Principal principal) {
        List<Transaction> transactions = transactionService.findAllByUser(principal.getName());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/month")
    public ResponseEntity<List<Transaction>> findByMonth(
            @RequestParam int year,
            @RequestParam int month,
            Principal principal) {

        List<Transaction> transactions = transactionService.findByUserAndMonth(
                principal.getName(), year, month);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/sum")
    public ResponseEntity<BigDecimal> sum(
            @RequestParam TransactionType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate end,
            Principal principal) {

        BigDecimal total = transactionService.sumByUserAndType(
                principal.getName(), type, start, end);
        return ResponseEntity.ok(total);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Principal principal) {
        transactionService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
