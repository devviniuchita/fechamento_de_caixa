package com.seucodigo.fecharcaixa.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "receipts")
public class Receipt {
    
    @Id
    private String id;
    
    @NotNull
    private LocalDateTime dataHora;
    
    @NotBlank
    private String tipo; // DINHEIRO, PIX, CARTAO_CREDITO, CARTAO_DEBITO, OUTROS
    
    @NotNull
    private BigDecimal valor;
    
    private String numeroTransacao;
    private String bandeiraCartao;
    private String ultimosDigitosCartao;
    
    @NotNull
    private String cashClosingId;
    
    private String observacoes;
    
    // Campos para armazenamento do comprovante
    private String nomeArquivo;
    private String contentType;
    private String urlArquivo; // URL do Google Drive
} 