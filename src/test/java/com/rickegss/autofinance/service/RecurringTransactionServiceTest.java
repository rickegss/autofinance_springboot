package com.rickegss.autofinance.service;

import com.rickegss.autofinance.entity.Transaction;
import com.rickegss.autofinance.entity.TransactionType;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecurringTransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RecurringTransactionService service;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("user@test.com")
                .build();
    }

    private Transaction createTemplate(LocalDate referenceDate, int recurringDay) {
        return Transaction.builder()
                .id(1L)
                .description("Assinatura")
                .amount(BigDecimal.valueOf(49.90))
                .type(TransactionType.DESPESA)
                .category("Streaming")
                .date(referenceDate)
                .recurring(true)
                .recurringDay(recurringDay)
                .user(testUser)
                .build();
    }

    @Test
    void shouldGenerateTransactionForFutureDayInSameMonth() {
        LocalDate today = LocalDate.of(2026, 5, 11);
        int recurringDay = 15;
        Transaction template = createTemplate(today, recurringDay);

        when(transactionRepository.findAllActiveRecurring()).thenReturn(List.of(template));
        when(transactionRepository.existsByUserAndDateAndAmountAndTypeAndCategory(
                any(), any(), any(), any(), any())).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        service.generateRecurringTransactions(today);

        verify(transactionRepository, times(1)).save(argThat(tx ->
                tx.getDate().equals(LocalDate.of(2026, 5, recurringDay)) &&
                        !tx.getRecurring() &&
                        tx.getRecurringDay() == null
        ));
    }

    @Test
    void shouldGenerateTransactionForNextMonthWhenDayAlreadyPassed() {
        LocalDate today = LocalDate.of(2026, 5, 11);
        int recurringDay = 10;
        Transaction template = createTemplate(today, recurringDay);

        when(transactionRepository.findAllActiveRecurring()).thenReturn(List.of(template));
        when(transactionRepository.existsByUserAndDateAndAmountAndTypeAndCategory(
                any(), any(), any(), any(), any())).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        service.generateRecurringTransactions(today);

        verify(transactionRepository, times(1)).save(argThat(tx ->
                tx.getDate().equals(LocalDate.of(2026, 6, recurringDay)) &&
                        !tx.getRecurring()
        ));
    }

    @Test
    void shouldNotGenerateDuplicateTransaction() {
        LocalDate today = LocalDate.of(2026, 5, 11);
        int recurringDay = 15;
        Transaction template = createTemplate(today, recurringDay);

        when(transactionRepository.findAllActiveRecurring()).thenReturn(List.of(template));
        when(transactionRepository.existsByUserAndDateAndAmountAndTypeAndCategory(
                any(), any(), any(), any(), any())).thenReturn(true);

        service.generateRecurringTransactions(today);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldAdjustInvalidDayToLastDayOfMonth() {
        LocalDate today = LocalDate.of(2026, 2, 28);
        int recurringDay = 30;
        Transaction template = createTemplate(today, recurringDay);

        when(transactionRepository.findAllActiveRecurring()).thenReturn(List.of(template));
        when(transactionRepository.existsByUserAndDateAndAmountAndTypeAndCategory(
                any(), any(), any(), any(), any())).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        service.generateRecurringTransactions(today);

        verify(transactionRepository, times(1)).save(argThat(tx ->
                tx.getDate().equals(LocalDate.of(2026, 2, 28)) // último dia de fevereiro
        ));
    }
}