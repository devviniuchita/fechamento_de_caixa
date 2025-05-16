
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
    
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;
    
    @NotNull(message = "Initial cash is required")
    @PositiveOrZero(message = "Initial cash must be zero or positive")
    private BigDecimal initialCash;
    
    @NotNull(message = "Sales amount is required")
    @PositiveOrZero(message = "Sales must be zero or positive")
    private BigDecimal sales;
    
    @PositiveOrZero(message = "Inserted change must be zero or positive")
    private BigDecimal insertedChange = BigDecimal.ZERO;
    
    @Valid
    @NotNull(message = "Payment methods are required")
    private PaymentMethodsDTO paymentMethods;
    
    @Valid
    private List<ExpenseDTO> expenses;
    
    private String receiptImageUrl;
    private BigDecimal totalIncome;
    private BigDecimal totalAssets;
    private boolean hasInconsistency;
    private String backupDriveId;
    
    @Data
    public static class PaymentMethodsDTO {
        @PositiveOrZero
        private BigDecimal cash = BigDecimal.ZERO;
        @PositiveOrZero
        private BigDecimal pix = BigDecimal.ZERO;
        @PositiveOrZero
        private BigDecimal deposit = BigDecimal.ZERO;
        @PositiveOrZero
        private BigDecimal withdrawal = BigDecimal.ZERO;
        @PositiveOrZero
        private BigDecimal giftCard = BigDecimal.ZERO;
        
        // Debit cards
        @PositiveOrZero
        private BigDecimal visaDebit = BigDecimal.ZERO;
        @PositiveOrZero
        private BigDecimal masterDebit = BigDecimal.ZERO;
        @PositiveOrZero
        private BigDecimal eloDebit = BigDecimal.ZERO;
        
        // Credit cards
        @PositiveOrZero
        private BigDecimal visaCredit = BigDecimal.ZERO;
        @PositiveOrZero
        private BigDecimal masterCredit = BigDecimal.ZERO;
        @PositiveOrZero
        private BigDecimal eloCredit = BigDecimal.ZERO;
        
        // Vouchers
        @PositiveOrZero
        private BigDecimal voucher = BigDecimal.ZERO;
    }
    
    @Data
    public static class ExpenseDTO {
        @NotNull(message = "Expense description is required")
        private String description;
        
        @NotNull(message = "Expense amount is required")
        @PositiveOrZero(message = "Expense amount must be zero or positive")
        private BigDecimal amount;
    }
} 