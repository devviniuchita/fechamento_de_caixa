package com.controle.fechamentocaixa.dto;

import java.math.BigDecimal;

import lombok.*;

/**
 * DTO para resultados de cálculos de fechamento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotaisFechamento {

  private BigDecimal totalEntradas;
  private BigDecimal totalAtivos;
  private BigDecimal totalDebito;
  private BigDecimal totalCredito;
  private BigDecimal totalCartoes;
  private BigDecimal totalDespesas;
  private BigDecimal totalCaixa;

  // Manual getters and setters
  public BigDecimal getTotalEntradas() {
    return totalEntradas;
  }

  public void setTotalEntradas(BigDecimal totalEntradas) {
    this.totalEntradas = totalEntradas;
  }

  public BigDecimal getTotalAtivos() {
    return totalAtivos;
  }

  public void setTotalAtivos(BigDecimal totalAtivos) {
    this.totalAtivos = totalAtivos;
  }

  public BigDecimal getTotalDebito() {
    return totalDebito;
  }

  public void setTotalDebito(BigDecimal totalDebito) {
    this.totalDebito = totalDebito;
  }

  public BigDecimal getTotalCredito() {
    return totalCredito;
  }

  public void setTotalCredito(BigDecimal totalCredito) {
    this.totalCredito = totalCredito;
  }

  public BigDecimal getTotalCartoes() {
    return totalCartoes;
  }

  public void setTotalCartoes(BigDecimal totalCartoes) {
    this.totalCartoes = totalCartoes;
  }

  public BigDecimal getTotalDespesas() {
    return totalDespesas;
  }

  public void setTotalDespesas(BigDecimal totalDespesas) {
    this.totalDespesas = totalDespesas;
  }

  public BigDecimal getTotalCaixa() {
    return totalCaixa;
  }

  public void setTotalCaixa(BigDecimal totalCaixa) {
    this.totalCaixa = totalCaixa;
  }
}
