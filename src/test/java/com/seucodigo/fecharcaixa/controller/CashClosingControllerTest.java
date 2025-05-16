package com.seucodigo.fecharcaixa.controller;

import com.seucodigo.fecharcaixa.model.CashClosing;
import com.seucodigo.fecharcaixa.model.CardPayments;
import com.seucodigo.fecharcaixa.service.CashClosingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CashClosingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CashClosingService cashClosingService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createCashClosing_ValidInput_ReturnsCreated() throws Exception {
        CashClosing cashClosing = createSampleCashClosing();
        when(cashClosingService.save(any(CashClosing.class))).thenReturn(cashClosing);

        mockMvc.perform(post("/api/cash-closing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cashClosing)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cashClosing.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getCashClosing_ExistingId_ReturnsCashClosing() throws Exception {
        CashClosing cashClosing = createSampleCashClosing();
        when(cashClosingService.findById("1")).thenReturn(Optional.of(cashClosing));

        mockMvc.perform(get("/api/cash-closing/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cashClosing.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getCashClosingsByDate_ValidDate_ReturnsList() throws Exception {
        CashClosing cashClosing = createSampleCashClosing();
        when(cashClosingService.findByDate(any(LocalDate.class)))
                .thenReturn(Arrays.asList(cashClosing));

        mockMvc.perform(get("/api/cash-closing/date/2025-04-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(cashClosing.getId()));
    }

    private CashClosing createSampleCashClosing() {
        CashClosing cashClosing = new CashClosing();
        cashClosing.setId("1");
        cashClosing.setClosingDate(LocalDate.of(2025, 4, 30));
        cashClosing.setResponsible("Test User");
        cashClosing.setInitialAmount(100.0);
        cashClosing.setSales(250.0);
        cashClosing.setChangeInserted(50.0);
        cashClosing.setCash(150.0);
        cashClosing.setPix(50.0);
        cashClosing.setDeposit(50.0);
        cashClosing.setWithdrawal(40.0);
        cashClosing.setVouchers(10.0);

        CardPayments debitCards = new CardPayments();
        debitCards.setVisa(10.0);
        debitCards.setMastercard(10.0);
        debitCards.setElo(10.0);
        cashClosing.setDebitCards(debitCards);

        CardPayments creditCards = new CardPayments();
        creditCards.setVisa(10.0);
        creditCards.setMastercard(10.0);
        creditCards.setElo(10.0);
        cashClosing.setCreditCards(creditCards);

        return cashClosing;
    }
} 