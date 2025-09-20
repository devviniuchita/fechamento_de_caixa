package com.controle.fechamentocaixa.model;

import java.math.BigDecimal;

import lombok.*;

/**
 * Value Object para formas de pagamento
 * Representa todas as formas de pagamento aceitas no fechamento de caixa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormasPagamento {

  @Builder.Default
  private BigDecimal dinheiro = BigDecimal.ZERO;

  @Builder.Default
  private BigDecimal pix = BigDecimal.ZERO;

  @Builder.Default
  private BigDecimal deposito = BigDecimal.ZERO;

  @Builder.Default
  private BigDecimal vale = BigDecimal.ZERO;

  @Builder.Default
  private BigDecimal sangria = BigDecimal.ZERO;

  private CartaoDebito debito;

  private CartaoCredito credito;

  @Builder.Default
  private BigDecimal voucher = BigDecimal.ZERO;

  // Manual getters and setters
  public BigDecimal getDinheiro() {
    return dinheiro;
  }

  public void setDinheiro(BigDecimal dinheiro) {
    this.dinheiro = dinheiro;
  }

  public BigDecimal getPix() {
    return pix;
  }

  public void setPix(BigDecimal pix) {
    this.pix = pix;
  }

  public BigDecimal getDeposito() {
    return deposito;
  }

  public void setDeposito(BigDecimal deposito) {
    this.deposito = deposito;
  }

  public BigDecimal getVale() {
    return vale;
  }

  public void setVale(BigDecimal vale) {
    this.vale = vale;
  }

  public BigDecimal getSangria() {
    return sangria;
  }

  public void setSangria(BigDecimal sangria) {
    this.sangria = sangria;
  }

  public CartaoDebito getDebito() {
    return debito;
  }

  public void setDebito(CartaoDebito debito) {
    this.debito = debito;
  }

  public CartaoCredito getCredito() {
    return credito;
  }

  public void setCredito(CartaoCredito credito) {
    this.credito = credito;
  }

  public BigDecimal getVoucher() {
    return voucher;
  }

  public void setVoucher(BigDecimal voucher) {
    this.voucher = voucher;
  }
}
