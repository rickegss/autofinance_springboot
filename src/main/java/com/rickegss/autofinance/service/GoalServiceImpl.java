package com.rickegss.autofinance.service;

import com.rickegss.autofinance.dto.GoalDTO;
import com.rickegss.autofinance.entity.Goal;
import com.rickegss.autofinance.entity.TransactionType;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.GoalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.rickegss.autofinance.repository.UserRepository;

import org.springframework.cglib.core.Local;
import org.springframework.security.access.AccessDeniedException;
import com.rickegss.autofinance.dto.GoalUpdateDTO;
import com.rickegss.autofinance.dto.TransactionDTO;
import com.rickegss.autofinance.dto.WithdrawDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService{
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public Goal create(String userEmail, GoalDTO dto){
        User user = findUserByEmail(userEmail);

        Goal goal = Goal.builder()
                .name(dto.name())
                .targetAmount(dto.targetAmount())
                .currentAmount(dto.currentAmount())
                .user(user)
                .build();
        return goalRepository.save(goal);
    }

    @Override
    public List<Goal> findAllByUser(String userEmail) {
        User user = findUserByEmail(userEmail);
        return goalRepository.findByUserOrderByNameAsc(user);
    }

    @Override
    @Transactional
    public void delete(Long goalId, String userEmail) {
        User user = findUserByEmail(userEmail);

        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new AccessDeniedException("Goal not found or access denied"));

        goalRepository.delete(goal);
    }

    @Override
    @Transactional
    public Goal addProgress(Long goalId, String userEmail, BigDecimal amount) {
        User user = findUserByEmail(userEmail);

        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new AccessDeniedException("Goal not found or access denied"));

        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        return goalRepository.save(goal);
    }

    @Override
    @Transactional
    public Goal update(Long goalId, String userEmail, GoalUpdateDTO dto) {
        User user = findUserByEmail(userEmail);
    
        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new AccessDeniedException("Goal not found or access denied"));
    
        if (dto.name() != null && !dto.name().isBlank()) {
            goal.setName(dto.name());
        }
        if (dto.targetAmount() != null) {
            goal.setTargetAmount(dto.targetAmount());
        }
    
        return goalRepository.save(goal);
    }

    @Override
    @Transactional
    public Goal withdraw(Long goalId, String userEmail, WithdrawDTO dto){
        User user = findUserByEmail(userEmail);

        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                                    .orElseThrow(() -> new AccessDeniedException("Goal not found or access denied."));

        BigDecimal amount = dto.amount();

        if (amount.compareTo(goal.getCurrentAmount()) <= 0){
            goal.setCurrentAmount(goal.getCurrentAmount().subtract(amount));

            TransactionDTO transactionDTO = new TransactionDTO(
                dto.description(), 
                amount,
                TransactionType.RECEITA, 
                "Poupança",
                dto.withdrawDate() != null ? dto.withdrawDate() : LocalDate.now(), 
                false, null, null);
            
            transactionService.create(userEmail, transactionDTO);

            return goalRepository.save(goal); 
        } else {
            throw new IllegalArgumentException("Insuficient funds in goal.");
        }
        

    }
}
