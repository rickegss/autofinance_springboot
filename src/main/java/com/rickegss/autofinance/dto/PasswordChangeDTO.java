package com.rickegss.autofinance.dto;

public record PasswordChangeDTO(String currentPassword, String newPassword, String confirmNewPassword) {}