package com.rickegss.autofinance.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rickegss.autofinance.dto.TransactionDTO;
import com.rickegss.autofinance.entity.TransactionType;
import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = User.builder()
                .email("teste@integrador.com")
                .password("senha123")
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser(username = "teste@integrador.com")
    void createTransaction_shouldReturnCreated() throws Exception {
        TransactionDTO dto = new TransactionDTO(
                "Aluguel",
                BigDecimal.valueOf(1200.00),
                TransactionType.DESPESA,
                "Moradia",
                LocalDate.now()
        );

        mockMvc.perform(post("/api/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Aluguel"))
                .andExpect(jsonPath("$.amount").value(1200.00))
                .andExpect(jsonPath("$.type").value("DESPESA"))
                .andExpect(jsonPath("$.category").value("Moradia"));
    }

    @Test
    @WithMockUser(username = "teste@integrador.com")
    void createTransaction_withInvalidData_shouldReturnBadRequest() throws Exception {
        TransactionDTO dto = new TransactionDTO(
                null,   // description
                null,   // amount
                null,   // type
                null,   // category
                null    // date
        );

        mockMvc.perform(post("/api/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "teste@integrador.com")
    void findAllTransactions_shouldReturnList() throws Exception {
        // Primeiro cria uma transação
        TransactionDTO dto = new TransactionDTO(
                "Salário",
                BigDecimal.valueOf(5000.00),
                TransactionType.RECEITA,
                "Trabalho",
                LocalDate.now()
        );

        mockMvc.perform(post("/api/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Salário"));
    }
}