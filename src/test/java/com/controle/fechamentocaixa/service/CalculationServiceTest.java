package com.controle.fechamentocaixa.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.controle.fechamentocaixa.dto.*;
import com.controle.fechamentocaixa.model.*;
import com.controle.fechamentocaixa.service.impl.CalculationServiceImpl;

/**
 * Testes unitários para CalculationService
 * Testa todos os cálculos financeiros com precisão BigDecimal
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CalculationService Tests")
class CalculationServiceTest {

  private CalculationService calculationService;

  @BeforeEach
  void setUp() {
    calculationService = new CalculationServiceImpl();
  }

  @Test
  @DisplayName("Deve calcular total de entradas corretamente")
  void deveCalcularTotalEntradas() {
    // Given
    BigDecimal caixaInicial = new BigDecimal("100.00");
    BigDecimal vendas = new BigDecimal("250.50");
    BigDecimal trocoInserido = new BigDecimal("50.25");

    // When
    BigDecimal total = calculationService.calcularTotalEntradas(caixaInicial, vendas, trocoInserido);

    // Then
    assertThat(total).isEqualByComparingTo(new BigDecimal("400.75"));
  }

  @Test
  @DisplayName("Deve calcular total de entradas com valores nulos como zero")
  void deveCalcularTotalEntradasComValoresNulos() {
    // Given
    BigDecimal caixaInicial = new BigDecimal("100.00");
    BigDecimal vendas = null;
    BigDecimal trocoInserido = new BigDecimal("50.00");

    // When
    BigDecimal total = calculationService.calcularTotalEntradas(caixaInicial, vendas, trocoInserido);

    // Then
    assertThat(total).isEqualByComparingTo(new BigDecimal("150.00"));
  }

  @Test
  @DisplayName("Deve calcular total de ativos corretamente")
  void deveCalcularTotalAtivos() {
    // Given
    FormasPagamento formas = new FormasPagamento();
    formas.setDinheiro(new BigDecimal("100.00"));
    formas.setPix(new BigDecimal("50.00"));
    formas.setDeposito(new BigDecimal("25.00"));
    formas.setVale(new BigDecimal("10.00"));
    formas.setSangria(new BigDecimal("15.00"));

    // When
    BigDecimal total = calculationService.calcularTotalAtivos(formas);

    // Then
    assertThat(total).isEqualByComparingTo(new BigDecimal("200.00"));
  }

  @Test
  @DisplayName("Deve calcular total de ativos com formas nulas")
  void deveCalcularTotalAtivosComFormasNulas() {
    // When
    BigDecimal total = calculationService.calcularTotalAtivos(null);

    // Then
    assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Deve calcular total de débito por bandeira")
  void deveCalcularTotalDebito() {
    // Given
    CartaoDebito debito = new CartaoDebito();
    debito.setVisa(new BigDecimal("30.00"));
    debito.setMaster(new BigDecimal("20.00"));
    debito.setElo(new BigDecimal("10.00"));

    // When
    BigDecimal total = calculationService.calcularTotalDebito(debito);

    // Then
    assertThat(total).isEqualByComparingTo(new BigDecimal("60.00"));
  }

  @Test
  @DisplayName("Deve calcular total de débito com cartão nulo")
  void deveCalcularTotalDebitoComCartaoNulo() {
    // When
    BigDecimal total = calculationService.calcularTotalDebito(null);

    // Then
    assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Deve calcular total de crédito por bandeira")
  void deveCalcularTotalCredito() {
    // Given
    CartaoCredito credito = new CartaoCredito();
    credito.setVisa(new BigDecimal("50.00"));
    credito.setMaster(new BigDecimal("40.00"));
    credito.setElo(new BigDecimal("20.00"));

    // When
    BigDecimal total = calculationService.calcularTotalCredito(credito);

    // Then
    assertThat(total).isEqualByComparingTo(new BigDecimal("110.00"));
  }

  @Test
  @DisplayName("Deve calcular total de cartões incluindo voucher")
  void deveCalcularTotalCartoes() {
    // Given
    CartaoDebito debito = new CartaoDebito();
    debito.setVisa(new BigDecimal("30.00"));
    debito.setMaster(new BigDecimal("20.00"));
    debito.setElo(new BigDecimal("10.00"));

    CartaoCredito credito = new CartaoCredito();
    credito.setVisa(new BigDecimal("50.00"));
    credito.setMaster(new BigDecimal("40.00"));
    credito.setElo(new BigDecimal("20.00"));

    BigDecimal voucher = new BigDecimal("15.00");

    // When
    BigDecimal total = calculationService.calcularTotalCartoes(debito, credito, voucher);

    // Then
    assertThat(total).isEqualByComparingTo(new BigDecimal("185.00"));
  }

  @Test
  @DisplayName("Deve calcular total de despesas")
  void deveCalcularTotalDespesas() {
    // Given
    Despesa despesa1 = new Despesa();
    despesa1.setDescricao("Água");
    despesa1.setValor(new BigDecimal("5.50"));

    Despesa despesa2 = new Despesa();
    despesa2.setDescricao("Café");
    despesa2.setValor(new BigDecimal("3.25"));

    // When
    BigDecimal total = calculationService.calcularTotalDespesas(Arrays.asList(despesa1, despesa2));

    // Then
    assertThat(total).isEqualByComparingTo(new BigDecimal("8.75"));
  }

  @Test
  @DisplayName("Deve calcular total de despesas com lista vazia")
  void deveCalcularTotalDespesasComListaVazia() {
    // When
    BigDecimal total = calculationService.calcularTotalDespesas(Collections.emptyList());

    // Then
    assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Deve calcular total de despesas com lista nula")
  void deveCalcularTotalDespesasComListaNula() {
    // When
    BigDecimal total = calculationService.calcularTotalDespesas(null);

    // Then
    assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Deve calcular todos os totais do fechamento")
  void deveCalcularTodosTotais() {
    // Given
    FechamentoRequest request = createValidFechamentoRequest();

    // When
    TotaisFechamento totais = calculationService.calcularTotais(request);

    // Then
    assertThat(totais.getTotalEntradas()).isEqualByComparingTo(new BigDecimal("400.00"));
    assertThat(totais.getTotalAtivos()).isEqualByComparingTo(new BigDecimal("175.00"));
    assertThat(totais.getTotalDebito()).isEqualByComparingTo(new BigDecimal("60.00"));
    assertThat(totais.getTotalCredito()).isEqualByComparingTo(new BigDecimal("110.00"));
    assertThat(totais.getTotalCartoes()).isEqualByComparingTo(new BigDecimal("185.00"));
    assertThat(totais.getTotalDespesas()).isEqualByComparingTo(new BigDecimal("5.50"));
    assertThat(totais.getTotalCaixa()).isEqualByComparingTo(new BigDecimal("-34.50"));
  }

  @Test
  @DisplayName("Deve verificar consistência como inconsistente quando delta > tolerância")
  void deveVerificarInconsistencia() {
    // Given
    TotaisFechamento totais = new TotaisFechamento();
    totais.setTotalCaixa(new BigDecimal("-34.50"));

    // When
    ConsistenciaResult resultado = calculationService.verificarConsistencia(totais);

    // Then
    assertThat(resultado.getConsistente()).isFalse();
    assertThat(resultado.getDelta()).isEqualByComparingTo(new BigDecimal("-34.50"));
    assertThat(resultado.getTolerancia()).isEqualByComparingTo(new BigDecimal("0.01"));
    assertThat(resultado.getMensagem()).contains("Diferença de R$ -34.50 encontrada");
  }

  @Test
  @DisplayName("Deve verificar consistência como consistente quando delta <= tolerância")
  void deveVerificarConsistencia() {
    // Given
    TotaisFechamento totais = new TotaisFechamento();
    totais.setTotalCaixa(new BigDecimal("0.01"));

    // When
    ConsistenciaResult resultado = calculationService.verificarConsistencia(totais);

    // Then
    assertThat(resultado.getConsistente()).isTrue();
    assertThat(resultado.getDelta()).isEqualByComparingTo(new BigDecimal("0.01"));
    assertThat(resultado.getMensagem()).isEqualTo("Fechamento consistente");
  }

  @Test
  @DisplayName("Deve verificar consistência como consistente quando delta é zero")
  void deveVerificarConsistenciaComDeltaZero() {
    // Given
    TotaisFechamento totais = new TotaisFechamento();
    totais.setTotalCaixa(BigDecimal.ZERO);

    // When
    ConsistenciaResult resultado = calculationService.verificarConsistencia(totais);

    // Then
    assertThat(resultado.getConsistente()).isTrue();
    assertThat(resultado.getDelta()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(resultado.getMensagem()).isEqualTo("Fechamento consistente");
  }

  // Método auxiliar para criar request válido
  private FechamentoRequest createValidFechamentoRequest() {
    CartaoDebito debito = new CartaoDebito();
    debito.setVisa(new BigDecimal("30.00"));
    debito.setMaster(new BigDecimal("20.00"));
    debito.setElo(new BigDecimal("10.00"));

    CartaoCredito credito = new CartaoCredito();
    credito.setVisa(new BigDecimal("50.00"));
    credito.setMaster(new BigDecimal("40.00"));
    credito.setElo(new BigDecimal("20.00"));

    FormasPagamento formas = new FormasPagamento();
    formas.setDinheiro(new BigDecimal("100.00"));
    formas.setPix(new BigDecimal("15.00"));
    formas.setDeposito(new BigDecimal("10.00"));
    formas.setVale(new BigDecimal("10.00"));
    formas.setSangria(new BigDecimal("40.00"));
    formas.setDebito(debito);
    formas.setCredito(credito);
    formas.setVoucher(new BigDecimal("15.00"));

    Despesa despesa = new Despesa();
    despesa.setDescricao("Água");
    despesa.setValor(new BigDecimal("5.50"));

    FechamentoRequest req = new FechamentoRequest();
    req.setData(LocalDate.now());
    req.setResponsavel("test-user");
    req.setCaixaInicial(new BigDecimal("100.00"));
    req.setVendas(new BigDecimal("250.00"));
    req.setTrocoInserido(new BigDecimal("50.00"));
    req.setFormasPagamento(formas);
    req.setDespesas(Arrays.asList(despesa));
    req.setObservacoes("Test closing");
    return req;
  }
}
