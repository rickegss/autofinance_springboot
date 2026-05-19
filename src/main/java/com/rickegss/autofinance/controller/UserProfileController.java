package com.rickegss.autofinance.controller;

import com.rickegss.autofinance.dto.PasswordChangeDTO;
import com.rickegss.autofinance.dto.ProfileUpdateDTO;
import com.rickegss.autofinance.entity.FinancialGoal;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Base64;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    public record UserDto(Long id, String name, String email,
                          FinancialGoal financialGoal, BigDecimal monthlyIncome,
                          String avatarBase64, String theme, Integer financialMonthDay) {}

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(toDto(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(@RequestBody ProfileUpdateDTO dto, Principal principal) {
        User updated = userService.updateProfile(principal.getName(), dto);
        return ResponseEntity.ok(toDto(updated));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody PasswordChangeDTO dto, Principal principal) {
        userService.changePassword(principal.getName(), dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/avatar")
    public ResponseEntity<Void> uploadAvatar(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        userService.updateAvatar(principal.getName(), file.getBytes());
        return ResponseEntity.ok().build();
    }

    private UserDto toDto(User user) {
        String avatarBase64 = null;
        if (user.getAvatar() != null && user.getAvatar().length > 0) {
            avatarBase64 = Base64.getEncoder().encodeToString(user.getAvatar());
        }
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getFinancialGoal(),
                user.getMonthlyIncome(),
                avatarBase64,
                user.getTheme(),
                user.getFinancialMonthDay()
        );
    }

    @PutMapping("/theme")
    public ResponseEntity<Void> updateTheme(@RequestParam String theme, Principal principal){
        userService.updateTheme(principal.getName(), theme);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/financial-month-day")
    public ResponseEntity<Void> updateFinancialMonthDay(@RequestParam Integer day, Principal principal) {
        userService.updateFinancialMonthDay(principal.getName(), day);
        return ResponseEntity.ok().build();
    }

}