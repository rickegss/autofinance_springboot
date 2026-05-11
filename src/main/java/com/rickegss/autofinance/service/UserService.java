package com.rickegss.autofinance.service;


import com.rickegss.autofinance.dto.PasswordChangeDTO;
import com.rickegss.autofinance.dto.ProfileUpdateDTO;
import com.rickegss.autofinance.entity.FinancialGoal;
import com.rickegss.autofinance.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    User register(User user);
    void saveUserCategories(String email, List<String> categories);
    void saveUserDetails(String email, FinancialGoal goal, BigDecimal income);
    User findByEmail(String email);
    User updateProfile(String email, ProfileUpdateDTO dto);
    void changePassword(String email, PasswordChangeDTO dto);
    void updateAvatar(String email, byte[] avatar);
}