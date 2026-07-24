package com.rickegss.autofinance.service;

import com.rickegss.autofinance.dto.GoalDTO;
import com.rickegss.autofinance.dto.TransactionDTO;
import com.rickegss.autofinance.dto.WithdrawDTO;
import com.rickegss.autofinance.entity.Goal;
import com.rickegss.autofinance.entity.TransactionType;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.GoalRepository;
import com.rickegss.autofinance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalServiceImpl goalService;

    private User user;
    private GoalDTO dto;
    private Goal goal;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@test.com")
                .password("pass123")
                .build();

        dto = new GoalDTO(
                "Emergency Fund",
                BigDecimal.valueOf(10000.00),
                BigDecimal.valueOf(3200.00)
        );

        goal = Goal.builder()
                .id(1L)
                .name("Emergency Fund")
                .targetAmount(BigDecimal.valueOf(10000.00))
                .currentAmount(BigDecimal.valueOf(3200.00))
                .user(user)
                .build();
    }

    @Test
    void create_shouldCreateGoalSuccessfully() {
        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));
        when(goalRepository.save(any(Goal.class)))
                .thenReturn(goal);

        Goal result = goalService.create("user@test.com", dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Emergency Fund");
        assertThat(result.getTargetAmount()).isEqualByComparingTo(BigDecimal.valueOf(10000.00));
        assertThat(result.getCurrentAmount()).isEqualByComparingTo(BigDecimal.valueOf(3200.00));
        assertThat(result.getUser()).isEqualTo(user);

        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    void create_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                goalService.create("unknown@test.com", dto))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

        verify(goalRepository, never()).save(any());
    }

    @Test
    void findAllByUser_shouldReturnUserGoals() {
        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));
        when(goalRepository.findByUserOrderByNameAsc(user))
                .thenReturn(List.of(goal));

        List<Goal> result = goalService.findAllByUser("user@test.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Emergency Fund");

        verify(goalRepository, times(1)).findByUserOrderByNameAsc(user);
    }

    @Test
    void addProgress_shouldUpdateCurrentAmountSuccessfully() {
        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));
        when(goalRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class)))
                .thenReturn(goal);

        Goal result = goalService.addProgress(1L, "user@test.com", BigDecimal.valueOf(500.00));

        assertThat(result.getCurrentAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(3700.00));

        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    void addProgress_shouldThrowAccessDeniedWhenGoalNotFound() {
        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));
        when(goalRepository.findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                goalService.addProgress(99L, "user@test.com", BigDecimal.valueOf(500.00)))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Goal not found or access denied");

        verify(goalRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteSuccessfully() {
        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));
        when(goalRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(goal));

        goalService.delete(1L, "user@test.com");

        verify(goalRepository, times(1)).delete(goal);
    }

    @Test
    void delete_shouldThrowAccessDeniedWhenGoalNotFound() {
        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));
        when(goalRepository.findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                goalService.delete(99L, "user@test.com"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Goal not found or access denied");

        verify(goalRepository, never()).delete(any());
    }

    @Test
    void withdraw_shouldSubtractAmountAndCreateTransaction() {
        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));
        when(goalRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class)))
                .thenReturn(goal);

        WithdrawDTO dto = new WithdrawDTO(BigDecimal.valueOf(500.00), LocalDate.now(), "Teste de Saque");


        Goal result = goalService.withdraw(1L, "user@test.com", dto);

        assertThat(result.getCurrentAmount()).isEqualByComparingTo(BigDecimal.valueOf(2700.00));

        ArgumentCaptor<TransactionDTO> captor = ArgumentCaptor.forClass(TransactionDTO.class);
        verify(transactionService, times(1)).create(eq("user@test.com"), captor.capture());

        TransactionDTO tx = captor.getValue();
        assertThat(tx.amount()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
        assertThat(tx.type()).isEqualTo(TransactionType.RECEITA);
        assertThat(tx.category()).isEqualTo("Poupança");
        assertThat(tx.description()).isEqualTo("Teste de Saque");
        assertThat(tx.date()).isEqualTo(LocalDate.now());
    }

    @Test
    void withdraw_shouldThrowWhenAmountExceedsCurrent() {
        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));
        when(goalRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(goal));

        WithdrawDTO dto = new WithdrawDTO(BigDecimal.valueOf(15000.0), LocalDate.now(), "Teste de Saque Acima do Saldo.");


        assertThatThrownBy(() -> goalService.withdraw(1L, "user@test.com", dto)).isInstanceOf(IllegalArgumentException.class)
                                                                                                  .hasMessage("Insuficient funds in goal.");
        

        verify(goalRepository, never()).save(any());
        verify(transactionService, never()).create(any(), any());
    }

    
    
}