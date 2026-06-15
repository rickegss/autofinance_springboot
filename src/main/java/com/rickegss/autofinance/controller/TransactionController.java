package com.rickegss.autofinance.controller;

import com.rickegss.autofinance.dto.CycleDataDTO;
import com.rickegss.autofinance.dto.TransactionDTO;
import com.rickegss.autofinance.entity.Transaction;
import com.rickegss.autofinance.entity.TransactionType;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.TransactionRepository;
import com.rickegss.autofinance.service.CycleService;
import com.rickegss.autofinance.service.TransactionService;
import com.rickegss.autofinance.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final CycleService cycleService;
    private final TransactionRepository transactionRepository;

    @PostMapping
    public ResponseEntity<Transaction> create(@Valid @RequestBody TransactionDTO dto, Principal principal) {
        Transaction created = transactionService.create(principal.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> findAll(Principal principal) {
        List<Transaction> transactions = transactionService.findAllByUser(principal.getName());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/month")
    public ResponseEntity<List<Transaction>> findByMonth(@RequestParam int year, @RequestParam int month, Principal principal) {
        List<Transaction> transactions = transactionService.findByUserAndMonth(principal.getName(), year, month);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/sum")
    public ResponseEntity<BigDecimal> sum(@RequestParam TransactionType type,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                          Principal principal) {
        BigDecimal total = transactionService.sumByUserAndType(principal.getName(), type, start, end);
        return ResponseEntity.ok(total);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        transactionService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current-cycle")
    public ResponseEntity<CycleDataDTO> getCurrentCycleData(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        LocalDate today = LocalDate.now();

        CycleService.FinancialCycle cycle = cycleService.getCurrentCycle(user, today);

        List<Transaction> transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user, cycle.start(), cycle.end());

        BigDecimal totalIncome = sumByType(transactions, TransactionType.RECEITA);
        BigDecimal totalExpenses = sumByType(transactions, TransactionType.DESPESA);
        BigDecimal monthlyIncome = user.getMonthlyIncome() != null ? user.getMonthlyIncome() : BigDecimal.ZERO;
        BigDecimal effectiveIncome = monthlyIncome.add(totalIncome);
        BigDecimal savings = effectiveIncome.subtract(totalExpenses);

        BigDecimal expensesPercentage = BigDecimal.ZERO;
        BigDecimal savingsPercentage = BigDecimal.ZERO;
        if (effectiveIncome.compareTo(BigDecimal.ZERO) > 0) {
            expensesPercentage = totalExpenses.multiply(BigDecimal.valueOf(100))
                    .divide(effectiveIncome, 2, RoundingMode.HALF_UP);
            savingsPercentage = savings.multiply(BigDecimal.valueOf(100))
                    .divide(effectiveIncome, 2, RoundingMode.HALF_UP);
        }

        Map<String, BigDecimal> expensesByCategory = transactions.stream()
                .filter(t -> t.getType() == TransactionType.DESPESA)
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

        expensesByCategory = expensesByCategory.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        CycleDataDTO dto = new CycleDataDTO(cycle.start(), cycle.end(), totalIncome, totalExpenses, savings,
                savingsPercentage, expensesPercentage, expensesByCategory, transactions);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/cycles")
    public ResponseEntity<List<Map<String, LocalDate>>> getUserCycles(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Transaction> allTransactions = transactionRepository.findByUserOrderByDateDesc(user);
        Set<CycleService.FinancialCycle> cycles = new TreeSet<>(Comparator.comparing(CycleService.FinancialCycle::start));
        for (Transaction t : allTransactions) {
            CycleService.FinancialCycle cycle = cycleService.getCycleContainingDate(user, t.getDate());
            cycles.add(cycle);
        }
        List<Map<String, LocalDate>> result = cycles.stream()
                .map(c -> Map.of("start", c.start(), "end", c.end()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/cycle-data")
    public ResponseEntity<CycleDataDTO> getCycleData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Principal principal) {
        
        User user = userService.findByEmail(principal.getName());
        List<Transaction> transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user, start, end);

        BigDecimal totalIncome = sumByType(transactions, TransactionType.RECEITA);
        BigDecimal totalExpenses = sumByType(transactions, TransactionType.DESPESA);
        BigDecimal monthlyIncome = user.getMonthlyIncome() != null ? user.getMonthlyIncome() : BigDecimal.ZERO;
        BigDecimal effectiveIncome = monthlyIncome.add(totalIncome);
        BigDecimal savings = effectiveIncome.subtract(totalExpenses);

        BigDecimal expensesPercentage = BigDecimal.ZERO;
        BigDecimal savingsPercentage = BigDecimal.ZERO;
        
        if (effectiveIncome.compareTo(BigDecimal.ZERO) > 0) {
            expensesPercentage = totalExpenses.multiply(BigDecimal.valueOf(100))
                    .divide(effectiveIncome, 2, RoundingMode.HALF_UP);
            savingsPercentage = savings.multiply(BigDecimal.valueOf(100))
                    .divide(effectiveIncome, 2, RoundingMode.HALF_UP);
        }

        Map<String, BigDecimal> expensesByCategory = transactions.stream()
                .filter(t -> t.getType() == TransactionType.DESPESA)
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
        
        expensesByCategory = expensesByCategory.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));

        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(t -> new TransactionDTO(
                        t.getDescription(), 
                        t.getAmount(), 
                        t.getType(), 
                        t.getCategory(), 
                        t.getDate(), 
                        t.getRecurring(), 
                        t.getRecurringDay(), 
                        t.getRecurringEndDate()
                ))
                .toList();

        CycleDataDTO dto = new CycleDataDTO(start, end, totalIncome, totalExpenses, savings,
                savingsPercentage, expensesPercentage, expensesByCategory, transactionDTOs);
        
        return ResponseEntity.ok(dto);
    }

    private BigDecimal sumByType(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
