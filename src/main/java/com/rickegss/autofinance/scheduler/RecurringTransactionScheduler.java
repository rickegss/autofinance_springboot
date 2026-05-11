package com.rickegss.autofinance.scheduler;

import com.rickegss.autofinance.service.RecurringTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecurringTransactionScheduler {
    private final RecurringTransactionService recurringTransactionService;

    @Scheduled(cron = "0 0 0 * * *")
    public void execute() {
        log.info("Iniciando geração de transações recorrentes");
        recurringTransactionService.generateRecurringTransactions();
        log.info("Finalizada geração de transações recorrentes");
    }
}
