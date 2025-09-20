package com.controle.fechamentocaixa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.controle.fechamentocaixa.model.*;

import lombok.*;

/**
 * DTO para respostas de fechamento de caixa com totais calculados
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FechamentoResponse {

  private String id;
  private LocalDate data;
  private String responsavel;

  // Valores de entrada
  private BigDecimal caixaInicial;
  private BigDecimal vendas;
  private BigDecimal trocoInserido;

  // Formas de pagamento
  private FormasPagamento formasPagamento;

  // Despesas
  private List<Despesa> despesas;

  // Totais calculados
  private BigDecimal totalEntradas;
  private BigDecimal totalAtivos;
  private BigDecimal totalDebito;
  private BigDecimal totalCredito;
  private BigDecimal totalCartoes;
  private BigDecimal totalDespesas;
  private BigDecimal totalCaixa;

  // Informações de consistência
  private Boolean consistente;
  private BigDecimal delta;

  // Status e observações
  private StatusFechamento status;
  private String observacoes;
  private List<String> comprovantes;

  // Campos de auditoria
  private LocalDateTime criadoEm;
  private LocalDateTime atualizadoEm;
  private String criadoPor;
  private String atualizadoPor;

  // Builder pattern manual para compatibilidade
  public static FechamentoResponse builder() {
    return new FechamentoResponse();
  }

  public FechamentoResponse id(String id) {
    this.id = id;
    return this;
  }

  public FechamentoResponse data(LocalDate data) {
    this.data = data;
    return this;
  }

  public FechamentoResponse responsavel(String responsavel) {
    this.responsavel = responsavel;
    return this;
  }

  public FechamentoResponse caixaInicial(BigDecimal caixaInicial) {
    this.caixaInicial = caixaInicial;
    return this;
  }

  public FechamentoResponse vendas(BigDecimal vendas) {
    this.vendas = vendas;
    return this;
  }

  public FechamentoResponse trocoInserido(BigDecimal trocoInserido) {
    this.trocoInserido = trocoInserido;
    return this;
  }

  public FechamentoResponse formasPagamento(FormasPagamento formasPagamento) {
    this.formasPagamento = formasPagamento;
    return this;
  }

  public FechamentoResponse despesas(List<Despesa> despesas) {
    this.despesas = despesas;
    return this;
  }

  public FechamentoResponse totalEntradas(BigDecimal totalEntradas) {
    this.totalEntradas = totalEntradas;
    return this;
  }

  public FechamentoResponse totalAtivos(BigDecimal totalAtivos) {
    this.totalAtivos = totalAtivos;
    return this;
  }

  public FechamentoResponse totalDebito(BigDecimal totalDebito) {
    this.totalDebito = totalDebito;
    return this;
  }

  public FechamentoResponse totalCredito(BigDecimal totalCredito) {
    this.totalCredito = totalCredito;
    return this;
  }

  public FechamentoResponse totalCartoes(BigDecimal totalCartoes) {
    this.totalCartoes = totalCartoes;
    return this;
  }

  public FechamentoResponse totalDespesas(BigDecimal totalDespesas) {
    this.totalDespesas = totalDespesas;
    return this;
  }

  public FechamentoResponse totalCaixa(BigDecimal totalCaixa) {
    this.totalCaixa = totalCaixa;
    return this;
  }

  public FechamentoResponse consistente(Boolean consistente) {
    this.consistente = consistente;
    return this;
  }

  public FechamentoResponse delta(BigDecimal delta) {
    this.delta = delta;
    return this;
  }

  public FechamentoResponse status(StatusFechamento status) {
    this.status = status;
    return this;
  }

  public FechamentoResponse observacoes(String observacoes) {
    this.observacoes = observacoes;
    return this;
  }

  public FechamentoResponse comprovantes(List<String> comprovantes) {
    this.comprovantes = comprovantes;
    return this;
  }

  public FechamentoResponse criadoEm(LocalDateTime criadoEm) {
    this.criadoEm = criadoEm;
    return this;
  }

  public FechamentoResponse atualizadoEm(LocalDateTime atualizadoEm) {
    this.atualizadoEm = atualizadoEm;
    return this;
  }

  public FechamentoResponse criadoPor(String criadoPor) {
    this.criadoPor = criadoPor;
    return this;
  }

  public FechamentoResponse atualizadoPor(String atualizadoPor) {
    this.atualizadoPor = atualizadoPor;
    return this;
  }

  public FechamentoResponse build() {
    return this;
  }
}
