package com.rickegss.autofinance.controller;


import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model){
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "dashboard";
    }
}
