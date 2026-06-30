package com.rickegss.autofinance.controller;

import com.rickegss.autofinance.dto.GoalDTO;
import com.rickegss.autofinance.entity.Goal;
import com.rickegss.autofinance.service.GoalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rickegss.autofinance.dto.GoalUpdateDTO;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<Goal> create(
            @Valid @RequestBody GoalDTO dto,
            Principal principal) {

        Goal created = goalService.create(principal.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Goal>> findAll(Principal principal) {
        List<Goal> goals = goalService.findAllByUser(principal.getName());
        return ResponseEntity.ok(goals);
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<Goal> addProgress(
            @PathVariable Long id,
            @RequestParam
            @NotNull(message = "Amount is required")
            @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
            BigDecimal amount,
            Principal principal) {

        Goal updated = goalService.addProgress(id, principal.getName(), amount);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Principal principal) {

        goalService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Goal> update(
        @PathVariable Long id,
        @Valid @RequestBody GoalUpdateDTO dto,
        Principal principal) {
    Goal updated = goalService.update(id, principal.getName(), dto);
    return ResponseEntity.ok(updated);
    }
}
