package com.rickegss.autofinance.controller;

import com.rickegss.autofinance.dto.UserDTO;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    public String login(){return "login";}

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult result){
        if(result.hasErrors()){
            return "register";
        }
        User user = User.builder()
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .build();

        userService.register(user);
        return "redirect:/login?success";

    }

}
