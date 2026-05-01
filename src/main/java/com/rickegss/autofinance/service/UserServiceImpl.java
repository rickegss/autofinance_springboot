package com.rickegss.autofinance.service;

import com.rickegss.autofinance.entity.FinancialGoal;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
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
}
