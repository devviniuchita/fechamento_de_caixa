package com.controle.fechamentocaixa.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cash_closings")
public class CashClosing {
    
    @Id
    private String id;
    
    @NotNull(message = "Data e hora são obrigatórios")
    private LocalDateTime createdAt;
    
    @NotBlank(message = "ID do responsável é obrigatório")
    private String responsibleId;
    
    @NotBlank(message = "Nome do responsável é obrigatório")
    private String responsibleName;
    
    @NotNull(message = "Saldo inicial é obrigatório")
    @PositiveOrZero(message = "Saldo inicial deve ser zero ou positivo")
    private BigDecimal initialCash;
    
    @NotNull(message = "Valor das vendas é obrigatório")
    @PositiveOrZero(message = "Valor das vendas deve ser zero ou positivo")
    private BigDecimal sales;
    
    @PositiveOrZero(message = "Troco inserido deve ser zero ou positivo")
    private BigDecimal insertedChange = BigDecimal.ZERO;
    
    private PaymentMethods paymentMethods = new PaymentMethods();
    private List<Expense> expenses = new ArrayList<>();
    private String receiptImageUrl;
    private BigDecimal totalIncome;
    private BigDecimal totalAssets;
    private boolean hasInconsistency;
    private String backupDriveId;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethods {
        @PositiveOrZero
        private BigDecimal cash = BigDecimal.ZERO;
        
        @PositiveOrZero
        private BigDecimal pix = BigDecimal.ZERO;
        
        @PositiveOrZero
        private BigDecimal deposit = BigDecimal.ZERO;
        
        @PositiveOrZero
        private BigDecimal giftCard = BigDecimal.ZERO;
        
        @PositiveOrZero
        private BigDecimal withdrawal = BigDecimal.ZERO;
        
        @PositiveOrZero
        private BigDecimal visaDebit = BigDecimal.ZERO;
        
        @PositiveOrZero
        private BigDecimal masterDebit = BigDecimal.ZERO;
        
        @PositiveOrZero
        private BigDecimal eloDebit = BigDecimal.ZERO;
        
        @PositiveOrZero
        private BigDecimal visaCredit = BigDecimal.ZERO;
        
        @PositiveOrZero
        private BigDecimal masterCredit = BigDecimal.ZERO;
        
        @PositiveOrZero
        private BigDecimal eloCredit = BigDecimal.ZERO;
        
        @PositiveOrZero
        private BigDecimal voucher = BigDecimal.ZERO;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Expense {
        @NotBlank(message = "Descrição da despesa é obrigatória")
        private String description;
        
        @NotNull(message = "Valor da despesa é obrigatório")
        @PositiveOrZero(message = "Valor da despesa deve ser zero ou positivo")
        private BigDecimal amount;
        
        private String category;
        private String notes;
    }
} 