package com.rickegss.autofinance.controller;

import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WelcomeController {
    private final UserService userService;

    @GetMapping("/welcome")
    public String welcome(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        log.info("Usuário logado: {}", user.getEmail());
        log.debug("FinancialGoal: {}", user.getFinancialGoal());
        log.debug("MonthlyIncome: {}", user.getMonthlyIncome());
        log.debug("PreferredCategories size: {}", user.getPreferredCategories().size());

        boolean isConfigComplete = user.getFinancialGoal() != null
                && user.getMonthlyIncome() != null
                && !user.getPreferredCategories().isEmpty();

        log.debug("isConfigComplete: {}", isConfigComplete);

        if (isConfigComplete) {
            return "redirect:/dashboard";
        }
        return "welcome";
    }
}