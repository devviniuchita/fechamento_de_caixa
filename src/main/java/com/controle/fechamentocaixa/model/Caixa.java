package com.controle.fechamentocaixa.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

/**
 * Entity Caixa - Representa uma sessão de caixa
 * Conecta com MongoDB Atlas Azure East US 2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "caixas")
public class Caixa {

  @Id
  private String id;

  @Indexed
  private String usuarioId; // ID do usuário responsável

  private String nomeUsuario; // Nome do operador do caixa

  @Indexed
  private LocalDateTime dataAbertura;

  private LocalDateTime dataFechamento;

  private BigDecimal valorAbertura;

  private BigDecimal valorFechamento;

  private BigDecimal totalVendas;

  private BigDecimal totalDinheiro;

  private BigDecimal totalCartao;

  private BigDecimal totalPix;

  private BigDecimal totalCheque;

  private BigDecimal totalOutros;

  // Controles de diferença
  private BigDecimal diferencaCaixa; // Diferença encontrada no fechamento

  private String observacoes;

  @Builder.Default
  private StatusCaixa status = StatusCaixa.ABERTO;

  // Auditoria
  private String criadoPor;
  private String fechadoPor;
  private LocalDateTime dataCriacao;
  private LocalDateTime dataUltimaAtualizacao;

  // Listas de transações
  private List<String> transacaoIds; // IDs das transações do caixa

  // Métodos de conveniência
  public boolean isAberto() {
    return status == StatusCaixa.ABERTO;
  }

  public boolean isFechado() {
    return status == StatusCaixa.FECHADO;
  }

  public BigDecimal getTotalCalculado() {
    return totalDinheiro.add(totalCartao)
        .add(totalPix)
        .add(totalCheque)
        .add(totalOutros);
  }

  public BigDecimal getValorEsperado() {
    return valorAbertura.add(totalVendas);
  }
}
