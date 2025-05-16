package com.controle.fechamentocaixa.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "receipts")
public class Receipt {
    
    @Id
    private String id;
    
    @NotNull(message = "Data e hora são obrigatórios")
    private LocalDateTime createdAt;
    
    @NotBlank(message = "Tipo de pagamento é obrigatório")
    private String paymentType; // CASH, PIX, CREDIT_CARD, DEBIT_CARD, OTHERS
    
    @NotNull(message = "Valor é obrigatório")
    @PositiveOrZero(message = "Valor deve ser zero ou positivo")
    private BigDecimal amount;
    
    private String transactionNumber;
    private String cardBrand;
    private String lastFourDigits;
    
    @NotNull(message = "ID do fechamento de caixa é obrigatório")
    private String cashClosingId;
    
    private String notes;
    
    // Campos para armazenamento do comprovante
    private String fileName;
    private String contentType;
    private String driveFileUrl;
} 