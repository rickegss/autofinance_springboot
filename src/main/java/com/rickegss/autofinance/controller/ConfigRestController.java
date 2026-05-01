package com.rickegss.autofinance.controller;


import com.rickegss.autofinance.dto.ConfigDetailsDTO;
import com.rickegss.autofinance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigRestController {
    private final UserService userService;

    @PostMapping("/categories")
    public ResponseEntity<Void> saveCategories(@RequestBody List<String> selectedCategories, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        userService.saveUserCategories(principal.getName(), selectedCategories);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/details")
    public ResponseEntity<Void> saveDetails(@RequestBody ConfigDetailsDTO detailsDTO, Principal principal){
        if(principal == null) return ResponseEntity.status(401).build();

        userService.saveUserDetails(principal.getName(), detailsDTO.goal(), detailsDTO.income());
        return ResponseEntity.ok().build();

    }
}