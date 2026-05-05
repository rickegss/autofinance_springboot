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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
    @DisplayName("Should create successfully a Transaction")
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
    

    @Test
    @DisplayName("Should throw UserNotFoundException when requested by inhexistent user")
    void create_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail("unknown@test.com"))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> transactionService.create("unknown@test.com", dto))
              .isInstanceOf(IllegalArgumentException.class)
              .hasMessage("Usuário não encontrado.");

        verify(transactionRepository, never()).save(any());
    }
    

    @Test
    @DisplayName("Should delete successfully a transaction by the own user")
    void delete_shouldDeleteSuccessfully() {
        // Arrange
        Transaction transaction = Transaction.builder()
            .id(1L)
            .user(user)
            .build();

        when(transactionRepository.findById(1L))
            .thenReturn(Optional.of(transaction));

        // Act
        transactionService.delete(1L, "user@test.com");

        // Assert
        verify(transactionRepository, times(1)).delete(transaction);
    }


    @Test
    @DisplayName("Should throw AccessDeniedException when user is not the transaction owner")
    void delete_shouldThrowAccessDeniedWhenNotOwner() {
        // Arrange
        User otherUser = User.builder()
            .id(2L)
            .email("other@test.com")
            .build();

        Transaction transaction = Transaction.builder()
            .id(1L)
            .user(otherUser)
            .build();

        when(transactionRepository.findById(1L))
            .thenReturn(Optional.of(transaction));

        // Act & Assert
        assertThatThrownBy(() ->
               transactionService.delete(1L, "user@test.com"))
                .isInstanceOf(AccessDeniedException.class);

        verify(transactionRepository, never()).delete(any());
   }

   @Test
   @DisplayName("Should throw TransactionNotFoundException when requested inhexistent transaction")
   void delete_shouldThrowExceptionWhenTransactionNotFound() {
       // Arrange
       when(transactionRepository.findById(99L))
           .thenReturn(Optional.empty());

       // Act & Assert
       assertThatThrownBy(() ->
              transactionService.delete(99L, "user@test.com"))
              .isInstanceOf(IllegalArgumentException.class)
              .hasMessage("Transação não encontrada");

       verify(transactionRepository, never()).delete(any());
   }
}
