package com.rickegss.autofinance.service;

import com.rickegss.autofinance.dto.TransactionDTO;
import com.rickegss.autofinance.entity.Transaction;
import com.rickegss.autofinance.entity.TransactionType;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.TransactionRepository;
import com.rickegss.autofinance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User user;
    private TransactionDTO dto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@test.com")
                .password("pass123")
                .build();

        dto = new TransactionDTO(
                "Aluguel",
                BigDecimal.valueOf(1200.00),
                TransactionType.DESPESA,
                "Moradia",
                LocalDate.now()
        );
    }

    @Test
    void create_shouldCreateTransactionSuccessfully() {
        // Arrange
        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        Transaction savedTransaction = Transaction.builder()
                .id(1L)
                .description(dto.description())
                .amount(dto.amount())
                .type(dto.type())
                .category(dto.category())
                .date(dto.date())
                .user(user)
                .build();

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(savedTransaction);

        // Act
        Transaction result = transactionService.create("user@test.com", dto);

        //Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1200.00));
        assertThat(result.getType()).isEqualTo(TransactionType.DESPESA);
        assertThat(result.getUser()).isEqualTo(user);

        verify(transactionRepository, times(1)).save(any(Transaction.class));

    }

}
