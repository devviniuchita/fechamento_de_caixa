package com.controle.fechamentocaixa.integration;

import com.controle.fechamentocaixa.config.TestConfig;
import com.controle.fechamentocaixa.dto.CashClosingDTO;
import com.controle.fechamentocaixa.model.CashClosing;
import com.controle.fechamentocaixa.repository.CashClosingRepository;
import com.controle.fechamentocaixa.security.JwtTokenProvider;
import com.controle.fechamentocaixa.util.TestDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestConfig.class)
public class CashClosingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CashClosingRepository cashClosingRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private CashClosingDTO testCashClosingDTO;
    private String authToken;

    @BeforeEach
    void setUp() {
        testCashClosingDTO = TestDataBuilder.createTestCashClosingDTO();
        cashClosingRepository.deleteAll();
        
        // Gerar token JWT para testes
        authToken = "Bearer " + jwtTokenProvider.createToken("testuser", Arrays.asList("ROLE_CAIXA"));
    }

    @Test
    @WithMockUser(roles = "CAIXA")
    void whenCreateCashClosing_thenStatus201() throws Exception {
        mockMvc.perform(post("/api/cash-closings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authToken)
                .content(objectMapper.writeValueAsString(testCashClosingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saldoInicial", is(testCashClosingDTO.getSaldoInicial().doubleValue())))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "CAIXA")
    void whenGetCashClosing_thenStatus200() throws Exception {
        // Criar um fechamento de caixa primeiro
        CashClosing saved = cashClosingRepository.save(TestDataBuilder.createTestCashClosing());

        mockMvc.perform(get("/api/cash-closings/{id}", saved.getId())
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId())))
                .andExpect(jsonPath("$.saldoInicial", is(saved.getSaldoInicial().doubleValue())));
    }

    @Test
    @WithMockUser(roles = "GERENTE")
    void whenGetAllCashClosings_thenStatus200() throws Exception {
        // Criar alguns fechamentos de caixa
        cashClosingRepository.save(TestDataBuilder.createTestCashClosing());
        cashClosingRepository.save(TestDataBuilder.createTestCashClosing());

        mockMvc.perform(get("/api/cash-closings")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void whenUnauthorized_thenStatus401() throws Exception {
        mockMvc.perform(get("/api/cash-closings"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CAIXA")
    void whenInvalidInput_thenStatus400() throws Exception {
        testCashClosingDTO.setSaldoInicial(null); // Tornar inválido

        mockMvc.perform(post("/api/cash-closings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authToken)
                .content(objectMapper.writeValueAsString(testCashClosingDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CAIXA")
    void whenGetByPeriod_thenStatus200() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        mockMvc.perform(get("/api/cash-closings/period")
                .param("start", start.toString())
                .param("end", end.toString())
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isArray()));
    }
} 