package com.controle.fechamentocaixa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReceiptDTO {
    private String id;
    
    @NotNull(message = "Data e hora são obrigatórios")
    private LocalDateTime createdAt;
    
    @NotBlank(message = "Tipo de pagamento é obrigatório")
    private String paymentType;
    
    @NotNull(message = "Valor é obrigatório")
    @PositiveOrZero(message = "Valor deve ser zero ou positivo")
    private BigDecimal amount;
    
    private String transactionNumber;
    private String cardBrand;
    private String lastFourDigits;
    
    @NotNull(message = "ID do fechamento de caixa é obrigatório")
    private String cashClosingId;
    
    private String notes;
    private String fileName;
    private String contentType;
    private String driveFileUrl;
} 