package com.seucodigo.fecharcaixa.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "cash_closings")
public class CashClosing {
    @Id
    private String id;
    private LocalDate date;
    private LocalDateTime createdAt;
    private String responsibleId;
    private String responsibleName;
    private BigDecimal initialCash;
    private BigDecimal sales;
    private BigDecimal insertedChange;
    
    private PaymentMethods paymentMethods;
    private List<Expense> expenses;
    private String receiptImageUrl;
    
    private BigDecimal totalIncome;
    private BigDecimal totalAssets;
    private boolean hasInconsistency;
    private String backupDriveId;

    @Data
    public static class PaymentMethods {
        private BigDecimal cash;
        private BigDecimal pix;
        private BigDecimal deposit;
        private BigDecimal withdrawal;
        private BigDecimal giftCard;
        
        // Debit cards
        private BigDecimal visaDebit;
        private BigDecimal masterDebit;
        private BigDecimal eloDebit;
        
        // Credit cards
        private BigDecimal visaCredit;
        private BigDecimal masterCredit;
        private BigDecimal eloCredit;
        
        // Vouchers
        private BigDecimal voucher;
    }

    @Data
    public static class Expense {
        private String description;
        private BigDecimal amount;
    }
} 