package com.rickegss.autofinance.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rickegss.autofinance.dto.PasswordChangeDTO;
import com.rickegss.autofinance.dto.ProfileUpdateDTO;
import com.rickegss.autofinance.entity.FinancialGoal;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = User.builder()
                .email("teste@perfil.com")
                .password(passwordEncoder.encode("senha123"))
                .name("Teste")
                .financialGoal(FinancialGoal.BALANCED)
                .monthlyIncome(BigDecimal.valueOf(5000))
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser(username = "teste@perfil.com")
    void getCurrentUser_shouldReturnUserData() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("teste@perfil.com"))
                .andExpect(jsonPath("$.name").value("Teste"))
                .andExpect(jsonPath("$.financialGoal").value("BALANCED"));
    }

    @Test
    @WithMockUser(username = "teste@perfil.com")
    void updateProfile_shouldUpdateAndReturnUser() throws Exception {
        ProfileUpdateDTO dto = new ProfileUpdateDTO(
                "Novo Nome",
                "novoemail@perfil.com",
                FinancialGoal.INVESTMENT,
                BigDecimal.valueOf(10000)
        );

        mockMvc.perform(put("/api/user/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novo Nome"))
                .andExpect(jsonPath("$.email").value("novoemail@perfil.com"))
                .andExpect(jsonPath("$.financialGoal").value("INVESTMENT"))
                .andExpect(jsonPath("$.monthlyIncome").value(10000));
    }

    @Test
    @WithMockUser(username = "teste@perfil.com")
    void changePassword_withCorrectData_shouldReturnOk() throws Exception {
        PasswordChangeDTO dto = new PasswordChangeDTO("senha123", "novaSenha456", "novaSenha456");

        mockMvc.perform(put("/api/user/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "teste@perfil.com")
    void changePassword_withWrongCurrentPassword_shouldReturnBadRequest() throws Exception {
        PasswordChangeDTO dto = new PasswordChangeDTO("senhaErrada", "novaSenha456", "novaSenha456");

        mockMvc.perform(put("/api/user/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "teste@perfil.com")
    void changePassword_withMismatchNewPasswords_shouldReturnBadRequest() throws Exception {
        PasswordChangeDTO dto = new PasswordChangeDTO("senha123", "novaSenha456", "diferente");

        mockMvc.perform(put("/api/user/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}