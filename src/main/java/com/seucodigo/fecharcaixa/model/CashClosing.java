package com.seucodigo.fecharcaixa.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "cash_closings")
public class CashClosing {
    
    @Id
    private String id;
    
    @NotNull
    private LocalDateTime dataHora;
    
    @NotNull
    private String userId;
    
    @NotNull
    private BigDecimal saldoInicial;
    
    @NotNull
    private BigDecimal saldoFinal;
    
    private BigDecimal totalDinheiro = BigDecimal.ZERO;
    private BigDecimal totalPix = BigDecimal.ZERO;
    private BigDecimal totalCartaoCredito = BigDecimal.ZERO;
    private BigDecimal totalCartaoDebito = BigDecimal.ZERO;
    private BigDecimal totalOutros = BigDecimal.ZERO;
    
    private List<Receipt> comprovantes;
    
    private String observacoes;
    
    private boolean conferido = false;
    private String conferidoPor;
    private LocalDateTime dataConferencia;
} 