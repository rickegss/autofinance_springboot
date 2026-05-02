package com.rickegss.autofinance.controller;


import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model){
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        model.addAttribute("user", user);
        return "dashboard";
    }
}
