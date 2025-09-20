package com.controle.fechamentocaixa.dto;

import java.time.LocalDate;

import com.controle.fechamentocaixa.model.StatusFechamento;

/**
 * DTO para filtros de busca de fechamentos
 */
public class FechamentoFilter {

  private LocalDate dataInicio;
  private LocalDate dataFim;
  private String responsavel;
  private StatusFechamento status;
  private Boolean consistente;

  public FechamentoFilter() {
  }

  public FechamentoFilter(LocalDate dataInicio, LocalDate dataFim, String responsavel, StatusFechamento status,
      Boolean consistente) {
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.responsavel = responsavel;
    this.status = status;
    this.consistente = consistente;
  }

  // Getters
  public LocalDate getDataInicio() {
    return dataInicio;
  }

  public LocalDate getDataFim() {
    return dataFim;
  }

  public String getResponsavel() {
    return responsavel;
  }

  public StatusFechamento getStatus() {
    return status;
  }

  public Boolean getConsistente() {
    return consistente;
  }

  // Setters
  public void setDataInicio(LocalDate dataInicio) {
    this.dataInicio = dataInicio;
  }

  public void setDataFim(LocalDate dataFim) {
    this.dataFim = dataFim;
  }

  public void setResponsavel(String responsavel) {
    this.responsavel = responsavel;
  }

  public void setStatus(StatusFechamento status) {
    this.status = status;
  }

  public void setConsistente(Boolean consistente) {
    this.consistente = consistente;
  }
}
