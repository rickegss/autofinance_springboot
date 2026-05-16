package com.rickegss.autofinance.repository;

import com.rickegss.autofinance.entity.Goal;
import com.rickegss.autofinance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserOrderByNameAsc(User user);
    Optional<Goal> findByIdAndUser(Long id, User user);
}
