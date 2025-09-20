package com.controle.fechamentocaixa.model;

import java.math.BigDecimal;

import lombok.*;

/**
 * Value Object para cartões de crédito por bandeira
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartaoCredito {

  @Builder.Default
  private BigDecimal visa = BigDecimal.ZERO;

  @Builder.Default
  private BigDecimal master = BigDecimal.ZERO;

  @Builder.Default
  private BigDecimal elo = BigDecimal.ZERO;

  /**
   * Calcula o total de cartões de crédito
   */
  public BigDecimal getTotal() {
    return visa.add(master).add(elo);
  }

  // Manual getters and setters
  public BigDecimal getVisa() {
    return visa;
  }

  public void setVisa(BigDecimal visa) {
    this.visa = visa;
  }

  public BigDecimal getMaster() {
    return master;
  }

  public void setMaster(BigDecimal master) {
    this.master = master;
  }

  public BigDecimal getElo() {
    return elo;
  }

  public void setElo(BigDecimal elo) {
    this.elo = elo;
  }
}
