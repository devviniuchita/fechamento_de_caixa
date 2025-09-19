package com.controle.fechamentocaixa.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

/**
 * Entity FechamentoCaixa - Representa o relatório de fechamento do caixa
 * Conecta com MongoDB Atlas Azure East US 2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "fechamentos")
public class FechamentoCaixa {

  @Id
  private String id;

  @Indexed
  private String caixaId; // ID do caixa fechado

  @Indexed
  private String usuarioId; // ID do usuário que fechou

  private String nomeUsuario;

  @Indexed
  private LocalDateTime dataFechamento;

  private LocalDateTime horaInicio;
  private LocalDateTime horaFim;

  // Valores de abertura
  private BigDecimal valorAbertura;

  // Totais por forma de pagamento
  private BigDecimal totalDinheiro;
  private BigDecimal totalCartaoCredito;
  private BigDecimal totalCartaoDebito;
  private BigDecimal totalPix;
  private BigDecimal totalCheque;
  private BigDecimal totalOutros;

  // Totais calculados
  private BigDecimal totalVendas;
  private BigDecimal totalEstornos;
  private BigDecimal totalSangrias;
  private BigDecimal totalReforcos;

  // Contagem física do caixa
  private ContadorDinheiro contadorDinheiro;

  // Diferenças encontradas
  private BigDecimal diferencaDinheiro;
  private BigDecimal diferencaTotal;

  // Status do fechamento
  @Builder.Default
  private StatusFechamento status = StatusFechamento.PENDENTE;

  // Observações e justificativas
  private String observacoes;
  private String justificativaDiferenca;

  // Lista de transações do período
  private List<String> transacaoIds;

  // Relatórios anexos
  private List<String> anexos; // URLs ou IDs de documentos

  // Auditoria
  private LocalDateTime dataCriacao;
  private String criadoPor;
  private String aprovadoPor;
  private LocalDateTime dataAprovacao;

  // Métodos de conveniência
  public BigDecimal getValorEsperado() {
    return valorAbertura
        .add(totalVendas)
        .subtract(totalEstornos)
        .subtract(totalSangrias)
        .add(totalReforcos);
  }

  public BigDecimal getValorContado() {
    return contadorDinheiro != null ? contadorDinheiro.getTotal() : BigDecimal.ZERO;
  }

  public boolean temDiferenca() {
    return diferencaTotal.compareTo(BigDecimal.ZERO) != 0;
  }
}

/**
 * Classe para contagem física do dinheiro
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ContadorDinheiro {
  private Integer notas100;
  private Integer notas50;
  private Integer notas20;
  private Integer notas10;
  private Integer notas5;
  private Integer notas2;

  private Integer moedas1;
  private Integer moedas050;
  private Integer moedas025;
  private Integer moedas010;
  private Integer moedas005;
  private Integer moedas001;

  public BigDecimal getTotal() {
    BigDecimal total = BigDecimal.ZERO;

    if (notas100 != null)
      total = total.add(BigDecimal.valueOf(notas100 * 100));
    if (notas50 != null)
      total = total.add(BigDecimal.valueOf(notas50 * 50));
    if (notas20 != null)
      total = total.add(BigDecimal.valueOf(notas20 * 20));
    if (notas10 != null)
      total = total.add(BigDecimal.valueOf(notas10 * 10));
    if (notas5 != null)
      total = total.add(BigDecimal.valueOf(notas5 * 5));
    if (notas2 != null)
      total = total.add(BigDecimal.valueOf(notas2 * 2));

    if (moedas1 != null)
      total = total.add(BigDecimal.valueOf(moedas1));
    if (moedas050 != null)
      total = total.add(BigDecimal.valueOf(moedas050 * 0.50));
    if (moedas025 != null)
      total = total.add(BigDecimal.valueOf(moedas025 * 0.25));
    if (moedas010 != null)
      total = total.add(BigDecimal.valueOf(moedas010 * 0.10));
    if (moedas005 != null)
      total = total.add(BigDecimal.valueOf(moedas005 * 0.05));
    if (moedas001 != null)
      total = total.add(BigDecimal.valueOf(moedas001 * 0.01));

    return total;
  }
}
