package com.controle.fechamentocaixa.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para criação de fechamento diário
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FechamentoDiarioRequest {

  @NotNull(message = "Responsável é obrigatório")
  private String responsavel;

  @NotNull(message = "Data é obrigatória")
  private LocalDate data;

  // Getters e Setters adicionais para compatibilidade
  public LocalDate getData() {
    return data;
  }

  public void setData(LocalDate data) {
    this.data = data;
  }

  public String getResponsavel() {
    return responsavel;
  }

  public void setResponsavel(String responsavel) {
    this.responsavel = responsavel;
  }

  // Builder pattern para compatibilidade
  public static FechamentoDiarioRequest builder() {
    return new FechamentoDiarioRequest();
  }

  public FechamentoDiarioRequest data(LocalDate data) {
    this.data = data;
    return this;
  }

  public FechamentoDiarioRequest responsavel(String responsavel) {
    this.responsavel = responsavel;
    return this;
  }

  public FechamentoDiarioRequest build() {
    return this;
  }
}
