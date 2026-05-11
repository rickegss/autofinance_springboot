package com.rickegss.autofinance.service;

import com.rickegss.autofinance.dto.PasswordChangeDTO;
import com.rickegss.autofinance.dto.ProfileUpdateDTO;
import com.rickegss.autofinance.entity.FinancialGoal;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("original@test.com")
                .password("encodedOldPassword")
                .name("Old Name")
                .financialGoal(FinancialGoal.BALANCED)
                .monthlyIncome(BigDecimal.valueOf(5000))
                .build();
    }

    @Test
    void updateProfile_shouldUpdateUserFields() {
        when(userRepository.findByEmail("original@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        ProfileUpdateDTO dto = new ProfileUpdateDTO(
                "New Name",
                "newemail@test.com",
                FinancialGoal.INVESTMENT,
                BigDecimal.valueOf(10000)
        );

        User updated = userService.updateProfile("original@test.com", dto);

        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getEmail()).isEqualTo("newemail@test.com");
        assertThat(updated.getFinancialGoal()).isEqualTo(FinancialGoal.INVESTMENT);
        assertThat(updated.getMonthlyIncome()).isEqualByComparingTo(BigDecimal.valueOf(10000));
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_shouldEncodeAndSaveNewPassword() {
        when(userRepository.findByEmail("original@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("current123", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("new456")).thenReturn("encodedNew456");
        when(userRepository.save(any(User.class))).thenReturn(user);

        PasswordChangeDTO dto = new PasswordChangeDTO("current123", "new456", "new456");
        userService.changePassword("original@test.com", dto);

        assertThat(user.getPassword()).isEqualTo("encodedNew456");
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_shouldThrowWhenCurrentPasswordMismatch() {
        when(userRepository.findByEmail("original@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedOldPassword")).thenReturn(false);

        PasswordChangeDTO dto = new PasswordChangeDTO("wrong", "new456", "new456");
        assertThatThrownBy(() -> userService.changePassword("original@test.com", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Senha atual incorreta");
    }

    @Test
    void changePassword_shouldThrowWhenNewPasswordsDontMatch() {
        PasswordChangeDTO dto = new PasswordChangeDTO("current123", "new456", "different");
        assertThatThrownBy(() -> userService.changePassword("original@test.com", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("As novas senhas não coincidem");
    }

    @Test
    void updateAvatar_shouldSaveBytes() {
        when(userRepository.findByEmail("original@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        byte[] avatarData = new byte[]{1, 2, 3};
        userService.updateAvatar("original@test.com", avatarData);

        assertThat(user.getAvatar()).isEqualTo(avatarData);
        verify(userRepository).save(user);
    }
}