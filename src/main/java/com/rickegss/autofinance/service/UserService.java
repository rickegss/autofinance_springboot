package com.rickegss.autofinance.service;


import com.rickegss.autofinance.entity.FinancialGoal;
import com.rickegss.autofinance.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    User register(User user);
    void saveUserCategories(String email, List<String> categories);
    void saveUserDetails(String email, FinancialGoal goal, BigDecimal income);
}