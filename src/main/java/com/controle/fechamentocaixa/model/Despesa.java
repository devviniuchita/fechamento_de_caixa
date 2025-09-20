package com.controle.fechamentocaixa.model;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Value Object para despesas do fechamento de caixa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Despesa {

  @NotBlank(message = "Descrição da despesa é obrigatória")
  private String descricao;

  @NotNull(message = "Valor da despesa é obrigatório")
  @DecimalMin(value = "0.01", message = "Valor da despesa deve ser maior que zero")
  private BigDecimal valor;

  // Manual getters and setters
  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }
}
