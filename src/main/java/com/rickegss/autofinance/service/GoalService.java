package com.rickegss.autofinance.service;

import com.rickegss.autofinance.dto.GoalDTO;
import com.rickegss.autofinance.entity.Goal;

import java.math.BigDecimal;
import java.util.List;

public interface GoalService {
    Goal create(String userEmail, GoalDTO dto);
    List<Goal> findAllByUser(String userEmail);
    Goal addProgress(Long goalId, String userEmail, BigDecimal amount);
    void delete(Long goalId, String userEmail);
}
