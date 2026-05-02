package com.rickegss.autofinance.repository;

import com.rickegss.autofinance.entity.Transaction;
import com.rickegss.autofinance.entity.TransactionType;
import com.rickegss.autofinance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserOrderByDateDesc(User user);
    List<Transaction> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate start, LocalDate end);
    List<Transaction> findByUserAndTypeOrderByDateDesc(User user, TransactionType type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.type = :type AND t.date BETWEEN :start AND :end")
    BigDecimal sumByUserAndTypeAndDateBetween(@Param("user") User user, @Param("type") TransactionType type, @Param("start") LocalDate start, @Param("end") LocalDate end);

}
