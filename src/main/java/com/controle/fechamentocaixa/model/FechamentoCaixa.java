package com.controle.fechamentocaixa.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * Entity FechamentoCaixa - Representa o fechamento de caixa diário
 * Segue o design especificado com campos de entrada, formas de pagamento e
 * totais calculados
 */
@Document(collection = "fechamentos")
@CompoundIndex(name = "responsavel_data_idx", def = "{'responsavel': 1, 'data': 1}", unique = true)
public class FechamentoCaixa {

  @Id
  private String id;

  @Indexed
  @NotNull(message = "Data é obrigatória")
  private LocalDate data;

  @Indexed
  @NotNull(message = "Responsável é obrigatório")
  private String responsavel;

  // Valores de entrada
  @NotNull(message = "Caixa inicial é obrigatório")
  @DecimalMin(value = "0.00", message = "Caixa inicial deve ser não-negativo")
  private BigDecimal caixaInicial = BigDecimal.ZERO;

  @NotNull(message = "Vendas é obrigatório")
  @DecimalMin(value = "0.00", message = "Vendas deve ser não-negativo")
  private BigDecimal vendas = BigDecimal.ZERO;

  @NotNull(message = "Troco inserido é obrigatório")
  @DecimalMin(value = "0.00", message = "Troco inserido deve ser não-negativo")
  private BigDecimal trocoInserido = BigDecimal.ZERO;

  // Formas de pagamento
  private FormasPagamento formasPagamento;

  // Despesas
  private List<Despesa> despesas;

  // Totais calculados
  private BigDecimal totalEntradas = BigDecimal.ZERO;
  private BigDecimal totalAtivos = BigDecimal.ZERO;
  private BigDecimal totalDebito = BigDecimal.ZERO;
  private BigDecimal totalCredito = BigDecimal.ZERO;
  private BigDecimal totalCartoes = BigDecimal.ZERO;
  private BigDecimal totalDespesas = BigDecimal.ZERO;
  private BigDecimal totalCaixa = BigDecimal.ZERO;

  // Consistência
  private Boolean consistente = false;
  private BigDecimal delta = BigDecimal.ZERO;

  // Status do fechamento
  private StatusFechamento status = StatusFechamento.ABERTO;

  // Observações e comprovantes
  private String observacoes;
  private List<String> comprovantes;

  // Campos de auditoria
  @Indexed
  private LocalDateTime criadoEm;

  private LocalDateTime atualizadoEm;

  private String criadoPor;

  private String atualizadoPor;

  /**
   * Verifica se o fechamento tem diferença (não é consistente)
   */
  public boolean temDiferenca() {
    return !consistente;
  }

  /**
   * Calcula o valor absoluto do delta para verificação de consistência
   */
  public BigDecimal getDeltaAbsoluto() {
    return delta != null ? delta.abs() : BigDecimal.ZERO;
  }

  // Getters e Setters adicionais para todos os campos
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

  public BigDecimal getDelta() {
    return delta;
  }

  public void setDelta(BigDecimal delta) {
    this.delta = delta;
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

  public void setStatus(StatusFechamento status) {
    this.status = status;
  }

  // Getters e Setters para campos de auditoria
  public void setCriadoEm(LocalDateTime criadoEm) {
    this.criadoEm = criadoEm;
  }

  public void setCriadoPor(String criadoPor) {
    this.criadoPor = criadoPor;
  }

  public void setAtualizadoEm(LocalDateTime atualizadoEm) {
    this.atualizadoEm = atualizadoEm;
  }

  public void setAtualizadoPor(String atualizadoPor) {
    this.atualizadoPor = atualizadoPor;
  }

  // Getters e Setters para outros campos que estão faltando
  public void setCaixaInicial(BigDecimal caixaInicial) {
    this.caixaInicial = caixaInicial;
  }

  public void setVendas(BigDecimal vendas) {
    this.vendas = vendas;
  }

  public void setTrocoInserido(BigDecimal trocoInserido) {
    this.trocoInserido = trocoInserido;
  }

  public void setFormasPagamento(FormasPagamento formasPagamento) {
    this.formasPagamento = formasPagamento;
  }

  public void setDespesas(List<Despesa> despesas) {
    this.despesas = despesas;
  }

  public void setConsistente(Boolean consistente) {
    this.consistente = consistente;
  }

  // Builder pattern estático para compatibilidade
  public static FechamentoCaixa builder() {
    return new FechamentoCaixa();
  }

  public FechamentoCaixa id(String id) {
    this.id = id;
    return this;
  }

  public FechamentoCaixa observacoes(String observacoes) {
    this.observacoes = observacoes;
    return this;
  }

  public FechamentoCaixa comprovantes(List<String> comprovantes) {
    this.comprovantes = comprovantes;
    return this;
  }

  // Getters que estão faltando
  public StatusFechamento getStatus() {
    return status;
  }

  public LocalDateTime getCriadoEm() {
    return criadoEm;
  }

  public LocalDateTime getAtualizadoEm() {
    return atualizadoEm;
  }

  public String getCriadoPor() {
    return criadoPor;
  }

  public String getAtualizadoPor() {
    return atualizadoPor;
  }

  // Método data para o builder
  public FechamentoCaixa data(LocalDate data) {
    this.data = data;
    return this;
  }

  // Setter para totalCaixa que estava faltando
  public void setTotalCaixa(BigDecimal totalCaixa) {
    this.totalCaixa = totalCaixa;
  }

  // Métodos do builder que estão faltando
  public FechamentoCaixa responsavel(String responsavel) {
    this.responsavel = responsavel;
    return this;
  }

  public FechamentoCaixa caixaInicial(BigDecimal caixaInicial) {
    this.caixaInicial = caixaInicial;
    return this;
  }

  public FechamentoCaixa vendas(BigDecimal vendas) {
    this.vendas = vendas;
    return this;
  }

  public FechamentoCaixa trocoInserido(BigDecimal trocoInserido) {
    this.trocoInserido = trocoInserido;
    return this;
  }

  public FechamentoCaixa formasPagamento(FormasPagamento formasPagamento) {
    this.formasPagamento = formasPagamento;
    return this;
  }

  public FechamentoCaixa despesas(List<Despesa> despesas) {
    this.despesas = despesas;
    return this;
  }

  public FechamentoCaixa status(StatusFechamento status) {
    this.status = status;
    return this;
  }

  public FechamentoCaixa build() {
    return this;
  }

  // Getters que ainda estão faltando - TODOS OS GETTERS NECESSÁRIOS
  public String getId() {
    return id;
  }

  public LocalDate getData() {
    return data;
  }

  public String getResponsavel() {
    return responsavel;
  }

  public BigDecimal getCaixaInicial() {
    return caixaInicial;
  }

  public BigDecimal getVendas() {
    return vendas;
  }

  public BigDecimal getTrocoInserido() {
    return trocoInserido;
  }

  public FormasPagamento getFormasPagamento() {
    return formasPagamento;
  }

  public List<Despesa> getDespesas() {
    return despesas;
  }

  public BigDecimal getTotalCaixa() {
    return totalCaixa;
  }

  public Boolean getConsistente() {
    return consistente;
  }
}
