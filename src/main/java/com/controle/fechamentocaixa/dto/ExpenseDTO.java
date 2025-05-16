package com.controle.fechamentocaixa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExpenseDTO {
    @NotBlank(message = "Descrição da despesa é obrigatória")
    private String description;
    
    @NotNull(message = "Valor da despesa é obrigatório")
    @PositiveOrZero(message = "Valor da despesa deve ser zero ou positivo")
    private BigDecimal amount;
} 