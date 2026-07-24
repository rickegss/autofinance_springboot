package com.rickegss.autofinance.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rickegss.autofinance.dto.GoalDTO;
import com.rickegss.autofinance.dto.WithdrawDTO;
import com.rickegss.autofinance.entity.Goal;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.GoalRepository;
import com.rickegss.autofinance.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class GoalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GoalRepository goalRepository;

    private Goal testGoal;
    
    private User testUser;

    @BeforeEach
    void setUp() {
        goalRepository.deleteAll();
        userRepository.deleteAll();
        testUser = User.builder()
                .email("teste@integrador.com")
                .password("senha123")
                .build();
        testUser = userRepository.save(testUser);
    
        testGoal = Goal.builder()
                       .name("Meta de Teste")
                       .targetAmount(BigDecimal.valueOf(10000.00))
                       .currentAmount(BigDecimal.valueOf(5000.00))
                       .user(testUser)
                       .build();
        testGoal = goalRepository.save(testGoal);
    }

    @Test
    @WithMockUser(username = "teste@integrador.com")
    void createGoal_shouldReturnCreated() throws Exception {
        GoalDTO goalDTO = new GoalDTO(
            "Teste", 
            BigDecimal.valueOf(10000.00), 
            BigDecimal.valueOf(5000.00));
        
        mockMvc.perform(post("/api/goals")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(goalDTO)))
                        .andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.name").value("Teste"))
                        .andExpect(jsonPath("$.targetAmount").value(10000.00))
                        .andExpect(jsonPath("$.currentAmount").value(5000.00));
        }
    
    @Test
    @WithMockUser(username = "teste@integrador.com")
    void withdrawGoal_shouldReturnOk() throws Exception {
        WithdrawDTO dto = new WithdrawDTO(BigDecimal.valueOf(2000.00), null, "teste");

        mockMvc.perform(patch("/api/goals/{id}/withdraw", testGoal.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser(username = "teste@integrador.com")
    void withdrawGoal_shouldReturnIllegalArgumentException_whenAmountExceedsBalance() throws Exception {
        WithdrawDTO dto = new WithdrawDTO(BigDecimal.valueOf(50000.00), LocalDate.now(), "teste com saque > saldo");

        mockMvc.perform(patch("/api/goals/{id}/withdraw", testGoal.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insuficient funds in goal."));

    }

}
