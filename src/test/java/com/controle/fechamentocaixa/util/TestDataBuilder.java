package com.controle.fechamentocaixa.util;

import com.controle.fechamentocaixa.model.User;
import com.controle.fechamentocaixa.model.CashClosing;
import com.controle.fechamentocaixa.model.Receipt;
import com.controle.fechamentocaixa.dto.CashClosingDTO;
import com.controle.fechamentocaixa.dto.PaymentMethodDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class TestDataBuilder {

    public static User createTestUser() {
        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser" + UUID.randomUUID().toString().substring(0, 8));
        user.setEmail("test" + UUID.randomUUID().toString().substring(0, 8) + "@test.com");
        user.setPassword("Test@123");
        user.setRoles(new HashSet<>(Arrays.asList("CAIXA")));
        user.setActive(true);
        return user;
    }

    public static CashClosing createTestCashClosing() {
        CashClosing cashClosing = new CashClosing();
        cashClosing.setSaldoInicial(new BigDecimal("1000.00"));
        cashClosing.setDataCriacao(LocalDateTime.now());
        cashClosing.setDataAtualizacao(LocalDateTime.now());
        cashClosing.setCriadoPor("testuser");
        return cashClosing;
    }

    public static CashClosingDTO createTestCashClosingDTO() {
        CashClosingDTO dto = new CashClosingDTO();
        dto.setSaldoInicial(new BigDecimal("1000.00"));
        
        PaymentMethodDTO payment = new PaymentMethodDTO();
        payment.setTipo("DINHEIRO");
        payment.setValor(new BigDecimal("500.00"));
        
        dto.setFormasPagamento(Arrays.asList(payment));
        dto.setObservacao("Teste de fechamento");
        return dto;
    }

    public static Receipt createTestReceipt() {
        Receipt receipt = new Receipt();
        receipt.setFileName("test-receipt.pdf");
        receipt.setFileUrl("https://drive.google.com/test-file");
        receipt.setUploadDate(LocalDateTime.now());
        receipt.setFileSize(1024L);
        return receipt;
    }
} 