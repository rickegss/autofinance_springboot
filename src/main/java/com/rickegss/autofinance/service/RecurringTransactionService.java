package com.rickegss.autofinance.service;

import com.rickegss.autofinance.entity.Transaction;
import com.rickegss.autofinance.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringTransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public void generateRecurringTransactions() {
        generateRecurringTransactions(LocalDate.now());
    }


    @Transactional
    public void generateRecurringTransactions(LocalDate today) {
        YearMonth currentMonth = YearMonth.from(today);
        int currentDay = today.getDayOfMonth();

        List<Transaction> templates = transactionRepository.findAllActiveRecurring();
        log.info("Verificando {} templates recorrentes para o mês {}/{}", templates.size(), currentMonth.getMonth(), currentMonth.getYear());

        for (Transaction template : templates) {
            Integer recurringDay = template.getRecurringDay();
            if (recurringDay == null) continue;

            LocalDate targetDate;
            try {
                if (recurringDay < currentDay) {
                    targetDate = today.plusMonths(1).withDayOfMonth(recurringDay);
                } else {
                    targetDate = today.withDayOfMonth(recurringDay);
                }
            } catch (DateTimeException e) {
                if (recurringDay < currentDay) {
                    targetDate = YearMonth.from(today.plusMonths(1)).atEndOfMonth();
                } else {
                    targetDate = YearMonth.from(today).atEndOfMonth();
                }
            }

            boolean alreadyExists = transactionRepository.existsByUserAndDateAndAmountAndTypeAndCategory(
                    template.getUser(), targetDate, template.getAmount(), template.getType(), template.getCategory()
            );

            if (!alreadyExists) {
                Transaction newTransaction = Transaction.builder()
                        .description(template.getDescription())
                        .amount(template.getAmount())
                        .type(template.getType())
                        .category(template.getCategory())
                        .date(targetDate)
                        .user(template.getUser())
                        .recurring(false)
                        .recurringDay(null)
                        .build();
                transactionRepository.save(newTransaction);
                log.info("Transação recorrente gerada: {} - {} para usuário {}, data {}",
                        template.getDescription(), newTransaction.getAmount(), template.getUser().getEmail(), targetDate);
            } else {
                log.debug("Transação recorrente já existe para o dia {}", targetDate);
            }
        }
    }
}