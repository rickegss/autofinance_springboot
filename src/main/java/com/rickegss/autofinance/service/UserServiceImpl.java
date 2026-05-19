package com.rickegss.autofinance.service;

import com.rickegss.autofinance.dto.PasswordChangeDTO;
import com.rickegss.autofinance.dto.ProfileUpdateDTO;
import com.rickegss.autofinance.entity.FinancialGoal;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(User user){
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void saveUserCategories(String email, List<String> categories){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        user.setPreferredCategories(categories);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void saveUserDetails(String email, FinancialGoal goal, BigDecimal income){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        user.setFinancialGoal(goal);
        user.setMonthlyIncome(income);
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Override
    @Transactional
    public User updateProfile(String email, ProfileUpdateDTO dto){
        User user = findByEmail(email);
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setFinancialGoal(dto.financialGoal());
        user.setMonthlyIncome(dto.monthlyIncome());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(String email, PasswordChangeDTO dto){
        if (!dto.newPassword().equals(dto.confirmNewPassword())) {
            throw new IllegalArgumentException("As novas senhas não coincidem");
        }
        User user = findByEmail(email);
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())){
            throw new IllegalArgumentException("Senha atual incorreta");
        }
        if (dto.newPassword().equals(user.getPassword())){
            throw new IllegalArgumentException("A nova senha não pode ser igual a anterior");
        }
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateAvatar(String email, byte[] avatar){
        User user = findByEmail(email);
        user.setAvatar(avatar);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateTheme(String email, String theme){
        User user = findByEmail(email);
        user.setTheme(theme);
        userRepository.save(user);
    }

}
