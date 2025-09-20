package com.controle.fechamentocaixa.dto;

import java.math.BigDecimal;

import lombok.*;

/**
 * DTO para resultado de validação de consistência
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsistenciaResult {

  private Boolean consistente;
  private BigDecimal delta;
  private BigDecimal tolerancia;
  private String mensagem;

  // Manual getters and setters
  public Boolean getConsistente() {
    return consistente;
  }

  public void setConsistente(Boolean consistente) {
    this.consistente = consistente;
  }

  public BigDecimal getDelta() {
    return delta;
  }

  public void setDelta(BigDecimal delta) {
    this.delta = delta;
  }

  public BigDecimal getTolerancia() {
    return tolerancia;
  }

  public void setTolerancia(BigDecimal tolerancia) {
    this.tolerancia = tolerancia;
  }

  public String getMensagem() {
    return mensagem;
  }

  public void setMensagem(String mensagem) {
    this.mensagem = mensagem;
  }
}
