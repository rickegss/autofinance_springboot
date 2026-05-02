package com.rickegss.autofinance.service;

import com.rickegss.autofinance.dto.TransactionDTO;
import com.rickegss.autofinance.entity.Transaction;
import com.rickegss.autofinance.entity.TransactionType;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.TransactionRepository;
import com.rickegss.autofinance.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Transaction create(String userEmail, TransactionDTO dto) {
        User user = findUserByEmail(userEmail);

        Transaction transaction = Transaction.builder()
                .description(dto.description())
                .amount(dto.amount())
                .type(dto.type())
                .category(dto.category())
                .date(dto.date())
                .user(user)
                .build();

        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> findAllByUser(String userEmail) {
        User user = findUserByEmail(userEmail);
        return transactionRepository.findByUserOrderByDateDesc(user);
    }

    @Override
    public List<Transaction> findByUserAndMonth(String userEmail, int year, int month) {
        User user = findUserByEmail(userEmail);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user, start, end);
    }

    @Override
    public BigDecimal sumByUserAndType(String userEmail, TransactionType type, LocalDate start, LocalDate end) {
        User user = findUserByEmail(userEmail);
        return transactionRepository.sumByUserAndTypeAndDateBetween(user, type, start, end);
    }

    @Override
    @Transactional
    public void delete(Long transactionId, String userEmail) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada"));

        if (!transaction.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Você não tem permissão para deletar esta transação");
        }

        transactionRepository.delete(transaction);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}