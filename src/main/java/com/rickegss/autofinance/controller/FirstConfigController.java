package com.rickegss.autofinance.controller;

import com.rickegss.autofinance.entity.Category;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/first-config")
@RequiredArgsConstructor
public class FirstConfigController {

    private final UserRepository userRepository;

    @GetMapping
    public String firstConfig(Model model) {
        List<Category> categories = List.of(
                new Category("Aluguel", "home"),
                new Category("Energia", "zap"),
                new Category("Água", "droplet"),
                new Category("Gás", "flame"),
                new Category("Internet", "wifi"),
                new Category("Transporte", "car"),
                new Category("Alimentação", "utensils"),
                new Category("Saúde", "heart-pulse"),
                new Category("Educação", "book-open"),
                new Category("Lazer", "gamepad-2"),
                new Category("Assinaturas", "monitor-play"),
                new Category("Compras", "shopping-bag"),
                new Category("Moda", "shirt"),
                new Category("Pet", "dog"),
                new Category("Hobby", "palette"),
                new Category("Casa", "sofa"),
                new Category("Viagem", "plane"),
                new Category("Presentes", "gift"),
                new Category("Investimentos", "trending-up"),
                new Category("Empréstimos", "landmark"),
                new Category("Seguro", "shield"),
                new Category("Impostos", "file-text"),
                new Category("Poupança", "piggy-bank"),
                new Category("Outros", "more-horizontal")
        );

        model.addAttribute("categories", categories);

        return "first-config";
    }

    @GetMapping("/details")
    public String details() {
        return "details";
    }

    @GetMapping("/summary")
    public String summary(Principal principal, Model model){
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        model.addAttribute("user", user);
        return "summary";
    }
}