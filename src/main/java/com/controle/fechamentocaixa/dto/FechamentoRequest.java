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
public class FechamentoRequest {

  @NotNull(message = "Data é obrigatória")
  private LocalDate data;

  @NotNull(message = "Responsável é obrigatório")
  private String responsavel;

  @NotNull(message = "Caixa inicial é obrigatório")
  @DecimalMin(value = "0.00", message = "Caixa inicial deve ser não-negativo")
  private BigDecimal caixaInicial = BigDecimal.ZERO;

  @NotNull(message = "Vendas é obrigatório")
  @DecimalMin(value = "0.00", message = "Vendas deve ser não-negativo")
  private BigDecimal vendas = BigDecimal.ZERO;

  @NotNull(message = "Troco inserido é obrigatório")
  @DecimalMin(value = "0.00", message = "Troco inserido deve ser não-negativo")
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

  /**
   * Builder pattern customizado para compatibilidade com código existente
   * Mantém comportamento esperado onde build() retorna this em vez de nova
   * instância
   */
  public static FechamentoRequestBuilder builder() {
    return new FechamentoRequestBuilder();
  }

  /**
   * Builder interno customizado que preserva comportamento legacy
   */
  public static class FechamentoRequestBuilder {
    private final FechamentoRequest instance = new FechamentoRequest();

    public FechamentoRequestBuilder data(LocalDate data) {
      instance.data = data;
      return this;
    }

    public FechamentoRequestBuilder responsavel(String responsavel) {
      instance.responsavel = responsavel;
      return this;
    }

    public FechamentoRequestBuilder caixaInicial(BigDecimal caixaInicial) {
      instance.caixaInicial = caixaInicial;
      return this;
    }

    public FechamentoRequestBuilder vendas(BigDecimal vendas) {
      instance.vendas = vendas;
      return this;
    }

    public FechamentoRequestBuilder trocoInserido(BigDecimal trocoInserido) {
      instance.trocoInserido = trocoInserido;
      return this;
    }

    public FechamentoRequestBuilder formasPagamento(FormasPagamento formasPagamento) {
      instance.formasPagamento = formasPagamento;
      return this;
    }

    public FechamentoRequestBuilder despesas(List<Despesa> despesas) {
      instance.despesas = despesas;
      return this;
    }

    public FechamentoRequestBuilder observacoes(String observacoes) {
      instance.observacoes = observacoes;
      return this;
    }

    public FechamentoRequestBuilder comprovantes(List<String> comprovantes) {
      instance.comprovantes = comprovantes;
      return this;
    }

    public FechamentoRequest build() {
      return instance; // Preserva comportamento esperado
    }
  }
}
