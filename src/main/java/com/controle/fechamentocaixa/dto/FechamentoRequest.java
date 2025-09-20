package com.controle.fechamentocaixa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.controle.fechamentocaixa.model.Despesa;
import com.controle.fechamentocaixa.model.FormasPagamento;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para requisições de criação e atualização de fechamento de caixa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FechamentoRequest {

  @NotNull(message = "Data é obrigatória")
  private LocalDate data;

  @NotNull(message = "Responsável é obrigatório")
  private String responsavel;

  @NotNull(message = "Caixa inicial é obrigatório")
  @DecimalMin(value = "0.00", message = "Caixa inicial deve ser não-negativo")
  @Builder.Default
  private BigDecimal caixaInicial = BigDecimal.ZERO;

  @NotNull(message = "Vendas é obrigatório")
  @DecimalMin(value = "0.00", message = "Vendas deve ser não-negativo")
  @Builder.Default
  private BigDecimal vendas = BigDecimal.ZERO;

  @NotNull(message = "Troco inserido é obrigatório")
  @DecimalMin(value = "0.00", message = "Troco inserido deve ser não-negativo")
  @Builder.Default
  private BigDecimal trocoInserido = BigDecimal.ZERO;

  @Valid
  private FormasPagamento formasPagamento;

  @Valid
  private List<Despesa> despesas;

  private String observacoes;

  private List<String> comprovantes;

  // Manual getters and setters to ensure compilation
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

  public BigDecimal getCaixaInicial() {
    return caixaInicial;
  }

  public void setCaixaInicial(BigDecimal caixaInicial) {
    this.caixaInicial = caixaInicial;
  }

  public BigDecimal getVendas() {
    return vendas;
  }

  public void setVendas(BigDecimal vendas) {
    this.vendas = vendas;
  }

  public BigDecimal getTrocoInserido() {
    return trocoInserido;
  }

  public void setTrocoInserido(BigDecimal trocoInserido) {
    this.trocoInserido = trocoInserido;
  }

  public FormasPagamento getFormasPagamento() {
    return formasPagamento;
  }

  public void setFormasPagamento(FormasPagamento formasPagamento) {
    this.formasPagamento = formasPagamento;
  }

  public List<Despesa> getDespesas() {
    return despesas;
  }

  public void setDespesas(List<Despesa> despesas) {
    this.despesas = despesas;
  }

  public String getObservacoes() {
    return observacoes;
  }

  public void setObservacoes(String observacoes) {
    this.observacoes = observacoes;
  }

  public List<String> getComprovantes() {
    return comprovantes;
  }

  public void setComprovantes(List<String> comprovantes) {
    this.comprovantes = comprovantes;
  }

  // Builder pattern para compatibilidade
  public static FechamentoRequest builder() {
    return new FechamentoRequest();
  }

  public FechamentoRequest data(LocalDate data) {
    this.data = data;
    return this;
  }

  public FechamentoRequest responsavel(String responsavel) {
    this.responsavel = responsavel;
    return this;
  }

  public FechamentoRequest caixaInicial(BigDecimal caixaInicial) {
    this.caixaInicial = caixaInicial;
    return this;
  }

  public FechamentoRequest vendas(BigDecimal vendas) {
    this.vendas = vendas;
    return this;
  }

  public FechamentoRequest trocoInserido(BigDecimal trocoInserido) {
    this.trocoInserido = trocoInserido;
    return this;
  }

  public FechamentoRequest formasPagamento(FormasPagamento formasPagamento) {
    this.formasPagamento = formasPagamento;
    return this;
  }

  public FechamentoRequest despesas(List<Despesa> despesas) {
    this.despesas = despesas;
    return this;
  }

  public FechamentoRequest observacoes(String observacoes) {
    this.observacoes = observacoes;
    return this;
  }

  public FechamentoRequest comprovantes(List<String> comprovantes) {
    this.comprovantes = comprovantes;
    return this;
  }

  public FechamentoRequest build() {
    return this;
  }
}
