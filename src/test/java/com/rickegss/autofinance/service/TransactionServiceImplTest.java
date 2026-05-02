package com.rickegss.autofinance.service;

import com.rickegss.autofinance.dto.TransactionDTO;
import com.rickegss.autofinance.entity.TransactionType;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.TransactionRepository;
import com.rickegss.autofinance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    // @Test -> void create_shouldCreateTransactionSuccessfully() ...

}
