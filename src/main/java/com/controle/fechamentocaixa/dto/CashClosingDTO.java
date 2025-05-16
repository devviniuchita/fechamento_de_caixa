package com.controle.fechamentocaixa.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CashClosingDTO {
    private String id;
    
    @NotNull(message = "Data é obrigatória")
    @PastOrPresent(message = "Data não pode ser futura")
    private LocalDate date;
    
    @NotNull(message = "Saldo inicial é obrigatório")
    @PositiveOrZero(message = "Saldo inicial deve ser zero ou positivo")
    private BigDecimal initialCash;
    
    @NotNull(message = "Valor das vendas é obrigatório")
    @PositiveOrZero(message = "Valor das vendas deve ser zero ou positivo")
    private BigDecimal sales;
    
    @PositiveOrZero(message = "Troco inserido deve ser zero ou positivo")
    private BigDecimal insertedChange = BigDecimal.ZERO;
    
    @Valid
    @NotNull(message = "Formas de pagamento são obrigatórias")
    private PaymentMethodsDTO paymentMethods;
    
    @Valid
    private List<ExpenseDTO> expenses;
    
    private String receiptImageUrl;
    private BigDecimal totalIncome;
    private BigDecimal totalAssets;
    private boolean hasInconsistency;
    private String backupDriveId;
} 