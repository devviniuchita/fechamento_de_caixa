package com.controle.fechamentocaixa.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.controle.fechamentocaixa.dto.*;
import com.controle.fechamentocaixa.exception.ResourceAlreadyExistsException;
import com.controle.fechamentocaixa.exception.ResourceNotFoundException;
import com.controle.fechamentocaixa.model.*;
import com.controle.fechamentocaixa.repository.FechamentoCaixaRepository;
import com.controle.fechamentocaixa.service.CalculationService;

/**
 * Testes unitários para FechamentoServiceImpl
 * Testa lógica de negócio, validações, cálculos e transições de status
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FechamentoServiceImpl Unit Tests")
class FechamentoServiceImplTest {

  @Mock
  private FechamentoCaixaRepository repository;

  @Mock
  private CalculationService calculationService;

  @InjectMocks
  private FechamentoServiceImpl fechamentoService;

  private FechamentoRequest validRequest;
  private FechamentoCaixa savedEntity;
  private TotaisFechamento totais;
  private ConsistenciaResult consistencia;

  @BeforeEach
  void setUp() {
    // Setup security context
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken("test@example.com", null));

    // Setup test data
    validRequest = createValidFechamentoRequest();
    savedEntity = createSavedEntity();
    totais = createTotaisFechamento();
    consistencia = createConsistenciaResult();
  }

  @Test
  @DisplayName("Deve criar fechamento com dados válidos")
  void deveCriarFechamentoComDadosValidos() {
    // Given
    when(repository.existsByResponsavelAndData(anyString(), any(LocalDate.class)))
        .thenReturn(false);
    when(calculationService.calcularTotais(any(FechamentoRequest.class)))
        .thenReturn(totais);
    when(calculationService.verificarConsistencia(any(TotaisFechamento.class)))
        .thenReturn(consistencia);
    when(repository.save(any(FechamentoCaixa.class)))
        .thenReturn(savedEntity);

    // When
    FechamentoResponse response = fechamentoService.criarFechamento(validRequest);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo("test-id");
    assertThat(response.getResponsavel()).isEqualTo("test@example.com");
    assertThat(response.getStatus()).isEqualTo(StatusFechamento.ABERTO);
    assertThat(response.getConsistente()).isTrue();

    verify(repository).existsByResponsavelAndData("test@example.com", LocalDate.now());
    verify(calculationService).calcularTotais(validRequest);
    verify(calculationService).verificarConsistencia(totais);
    verify(repository).save(any(FechamentoCaixa.class));
  }

  @Test
  @DisplayName("Deve rejeitar criação de fechamento duplicado")
  void deveRejeitarCriacaoFechamentoDuplicado() {
    // Given
    when(repository.existsByResponsavelAndData(anyString(), any(LocalDate.class)))
        .thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> fechamentoService.criarFechamento(validRequest))
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("Já existe fechamento para test@example.com");

    verify(repository).existsByResponsavelAndData("test@example.com", LocalDate.now());
    verifyNoMoreInteractions(repository, calculationService);
  }

  @Test
  @DisplayName("Deve atualizar fechamento com status ABERTO")
  void deveAtualizarFechamentoComStatusAberto() {
    // Given
    FechamentoCaixa existingEntity = createSavedEntity();
    existingEntity.setStatus(StatusFechamento.ABERTO);

    when(repository.findById("test-id")).thenReturn(Optional.of(existingEntity));
    when(calculationService.calcularTotais(any(FechamentoRequest.class)))
        .thenReturn(totais);
    when(calculationService.verificarConsistencia(any(TotaisFechamento.class)))
        .thenReturn(consistencia);
    when(repository.save(any(FechamentoCaixa.class)))
        .thenReturn(existingEntity);

    // When
    FechamentoResponse response = fechamentoService.atualizarFechamento("test-id", validRequest);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo("test-id");

    verify(repository).findById("test-id");
    verify(calculationService).calcularTotais(validRequest);
    verify(calculationService).verificarConsistencia(totais);
    verify(repository).save(existingEntity);
  }

  @Test
  @DisplayName("Deve rejeitar atualização de fechamento FECHADO")
  void deveRejeitarAtualizacaoFechamentoFechado() {
    // Given
    FechamentoCaixa existingEntity = createSavedEntity();
    existingEntity.setStatus(StatusFechamento.FECHADO);

    when(repository.findById("test-id")).thenReturn(Optional.of(existingEntity));

    // When & Then
    assertThatThrownBy(() -> fechamentoService.atualizarFechamento("test-id", validRequest))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Só é possível atualizar fechamentos com status ABERTO");

    verify(repository).findById("test-id");
    verifyNoMoreInteractions(calculationService, repository);
  }

  @Test
  @DisplayName("Deve buscar fechamento por ID")
  void deveBuscarFechamentoPorId() {
    // Given
    when(repository.findById("test-id")).thenReturn(Optional.of(savedEntity));

    // When
    FechamentoResponse response = fechamentoService.buscarPorId("test-id");

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo("test-id");
    assertThat(response.getResponsavel()).isEqualTo("test@example.com");

    verify(repository).findById("test-id");
  }

  @Test
  @DisplayName("Deve lançar exceção para fechamento inexistente")
  void deveLancarExcecaoParaFechamentoInexistente() {
    // Given
    when(repository.findById("invalid-id")).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> fechamentoService.buscarPorId("invalid-id"))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Fechamento não encontrado: invalid-id");

    verify(repository).findById("invalid-id");
  }

  @Test
  @DisplayName("Deve buscar fechamentos com filtro de data")
  void deveBuscarFechamentosComFiltroDeData() {
    // Given
    LocalDate dataInicio = LocalDate.now().minusDays(7);
    LocalDate dataFim = LocalDate.now();

    FechamentoFilter filter = new FechamentoFilter();
    filter.setDataInicio(dataInicio);
    filter.setDataFim(dataFim);

    Pageable pageable = PageRequest.of(0, 10);
    List<FechamentoCaixa> fechamentos = List.of(savedEntity);
    Page<FechamentoCaixa> pageResult = new PageImpl<>(fechamentos, pageable, 1);

    when(repository.findByDataBetweenOrderByDataDesc(dataInicio, dataFim, pageable))
        .thenReturn(pageResult);

    // When
    Page<FechamentoResponse> result = fechamentoService.buscarFechamentos(filter, pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getId()).isEqualTo("test-id");

    verify(repository).findByDataBetweenOrderByDataDesc(dataInicio, dataFim, pageable);
  }

  @Test
  @DisplayName("Deve buscar fechamentos com filtro de responsável")
  void deveBuscarFechamentosComFiltroResponsavel() {
    // Given
    FechamentoFilter filter = new FechamentoFilter();
    filter.setResponsavel("test@example.com");

    Pageable pageable = PageRequest.of(0, 10);
    List<FechamentoCaixa> fechamentos = List.of(savedEntity);
    Page<FechamentoCaixa> pageResult = new PageImpl<>(fechamentos, pageable, 1);

    when(repository.findAll(pageable)).thenReturn(pageResult);

    // When
    Page<FechamentoResponse> result = fechamentoService.buscarFechamentos(filter, pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getResponsavel()).isEqualTo("test@example.com");

    verify(repository).findAll(pageable);
  }

  @Test
  @DisplayName("Deve fechar fechamento com status ABERTO")
  void deveFecharFechamentoComStatusAberto() {
    // Given
    FechamentoCaixa existingEntity = createSavedEntity();
    existingEntity.setStatus(StatusFechamento.ABERTO);

    when(repository.findById("test-id")).thenReturn(Optional.of(existingEntity));
    when(repository.save(any(FechamentoCaixa.class))).thenReturn(existingEntity);

    // When
    FechamentoResponse response = fechamentoService.fecharFechamento("test-id");

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(StatusFechamento.FECHADO);

    verify(repository).findById("test-id");
    verify(repository).save(existingEntity);
    assertThat(existingEntity.getStatus()).isEqualTo(StatusFechamento.FECHADO);
  }

  @Test
  @DisplayName("Deve rejeitar fechamento de status não ABERTO")
  void deveRejeitarFechamentoStatusNaoAberto() {
    // Given
    FechamentoCaixa existingEntity = createSavedEntity();
    existingEntity.setStatus(StatusFechamento.FECHADO);

    when(repository.findById("test-id")).thenReturn(Optional.of(existingEntity));

    // When & Then
    assertThatThrownBy(() -> fechamentoService.fecharFechamento("test-id"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Só é possível fechar fechamentos com status ABERTO");

    verify(repository).findById("test-id");
    verifyNoMoreInteractions(repository);
  }

  @Test
  @DisplayName("Deve conferir fechamento com status FECHADO")
  void deveConferirFechamentoComStatusFechado() {
    // Given
    FechamentoCaixa existingEntity = createSavedEntity();
    existingEntity.setStatus(StatusFechamento.FECHADO);

    when(repository.findById("test-id")).thenReturn(Optional.of(existingEntity));
    when(repository.save(any(FechamentoCaixa.class))).thenReturn(existingEntity);

    // When
    FechamentoResponse response = fechamentoService.conferirFechamento("test-id");

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(StatusFechamento.CONFERIDO);

    verify(repository).findById("test-id");
    verify(repository).save(existingEntity);
    assertThat(existingEntity.getStatus()).isEqualTo(StatusFechamento.CONFERIDO);
  }

  @Test
  @DisplayName("Deve rejeitar conferência de status não FECHADO")
  void deveRejeitarConferenciaStatusNaoFechado() {
    // Given
    FechamentoCaixa existingEntity = createSavedEntity();
    existingEntity.setStatus(StatusFechamento.ABERTO);

    when(repository.findById("test-id")).thenReturn(Optional.of(existingEntity));

    // When & Then
    assertThatThrownBy(() -> fechamentoService.conferirFechamento("test-id"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Só é possível conferir fechamentos com status FECHADO");

    verify(repository).findById("test-id");
    verifyNoMoreInteractions(repository);
  }

  @Test
  @DisplayName("Deve validar fechamento recalculando totais")
  void deveValidarFechamentoRecalculandoTotais() {
    // Given
    when(repository.findById("test-id")).thenReturn(Optional.of(savedEntity));
    when(calculationService.calcularTotais(any(FechamentoRequest.class)))
        .thenReturn(totais);
    when(calculationService.verificarConsistencia(any(TotaisFechamento.class)))
        .thenReturn(consistencia);
    when(repository.save(any(FechamentoCaixa.class)))
        .thenReturn(savedEntity);

    // When
    FechamentoResponse response = fechamentoService.validarFechamento("test-id");

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo("test-id");

    verify(repository).findById("test-id");
    verify(calculationService).calcularTotais(any(FechamentoRequest.class));
    verify(calculationService).verificarConsistencia(totais);
    verify(repository).save(savedEntity);
  }

  @Test
  @DisplayName("Deve criar fechamento diário")
  void deveCriarFechamentoDiario() {
    // Given
    String responsavel = "test@example.com";
    LocalDate data = LocalDate.now();

    when(repository.existsByResponsavelAndData(responsavel, data))
        .thenReturn(false);
    when(calculationService.calcularTotais(any(FechamentoRequest.class)))
        .thenReturn(totais);
    when(calculationService.verificarConsistencia(any(TotaisFechamento.class)))
        .thenReturn(consistencia);
    when(repository.save(any(FechamentoCaixa.class)))
        .thenReturn(savedEntity);

    // When
    FechamentoResponse response = fechamentoService.criarFechamentoDiario(responsavel, data);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getResponsavel()).isEqualTo(responsavel);
    assertThat(response.getData()).isEqualTo(data);

    verify(repository, atLeastOnce()).existsByResponsavelAndData(responsavel, data);
    verify(repository).save(any(FechamentoCaixa.class));
  }

  @Test
  @DisplayName("Deve rejeitar fechamento diário duplicado")
  void deveRejeitarFechamentoDiarioDuplicado() {
    // Given
    String responsavel = "test@example.com";
    LocalDate data = LocalDate.now();

    when(repository.existsByResponsavelAndData(responsavel, data))
        .thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> fechamentoService.criarFechamentoDiario(responsavel, data))
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("Já existe fechamento para test@example.com");

    verify(repository).existsByResponsavelAndData(responsavel, data);
    verifyNoMoreInteractions(repository, calculationService);
  }

  @Test
  @DisplayName("Deve aplicar totais corretamente na entidade")
  void deveAplicarTotaisCorretamenteNaEntidade() {
    // Given
    when(repository.existsByResponsavelAndData(anyString(), any(LocalDate.class)))
        .thenReturn(false);
    when(calculationService.calcularTotais(any(FechamentoRequest.class)))
        .thenReturn(totais);
    when(calculationService.verificarConsistencia(any(TotaisFechamento.class)))
        .thenReturn(consistencia);

    // Return saved entity with ID
    when(repository.save(any(FechamentoCaixa.class)))
        .thenReturn(savedEntity);

    // When
    FechamentoResponse response = fechamentoService.criarFechamento(validRequest);

    // Then
    assertThat(response.getTotalEntradas()).isEqualTo(new BigDecimal("600.00"));
    assertThat(response.getTotalCaixa()).isEqualTo(new BigDecimal("590.00"));
    assertThat(response.getConsistente()).isTrue();
    assertThat(response.getDelta()).isEqualTo(BigDecimal.ZERO);
  }

  // Helper methods

  private FechamentoRequest createValidFechamentoRequest() {
    FechamentoRequest request = new FechamentoRequest();
    request.setData(LocalDate.now());
    request.setResponsavel("test@example.com");
    request.setCaixaInicial(new BigDecimal("100.00"));
    request.setVendas(new BigDecimal("500.00"));
    request.setTrocoInserido(new BigDecimal("50.00"));

    FormasPagamento formas = new FormasPagamento();
    formas.setDinheiro(new BigDecimal("200.00"));
    formas.setPix(new BigDecimal("150.00"));
    request.setFormasPagamento(formas);

    Despesa despesa = new Despesa();
    despesa.setDescricao("Teste");
    despesa.setValor(new BigDecimal("10.00"));
    request.setDespesas(List.of(despesa));

    request.setObservacoes("Teste unitário");

    return request;
  }

  private FechamentoCaixa createSavedEntity() {
    FechamentoCaixa entity = FechamentoCaixa.builder()
        .id("test-id")
        .data(LocalDate.now())
        .responsavel("test@example.com")
        .status(StatusFechamento.ABERTO)
        .build();

    entity.setCaixaInicial(new BigDecimal("100.00"));
    entity.setVendas(new BigDecimal("500.00"));
    entity.setTrocoInserido(new BigDecimal("50.00"));
    entity.setConsistente(true);
    entity.setDelta(BigDecimal.ZERO);
    entity.setCriadoEm(LocalDateTime.now());
    entity.setAtualizadoEm(LocalDateTime.now());
    entity.setCriadoPor("test@example.com");
    entity.setAtualizadoPor("test@example.com");

    // Apply totals from helper
    TotaisFechamento totais = createTotaisFechamento();
    entity.setTotalEntradas(totais.getTotalEntradas());
    entity.setTotalCaixa(totais.getTotalCaixa());
    entity.setTotalDespesas(totais.getTotalDespesas());

    return entity;
  }

  private TotaisFechamento createTotaisFechamento() {
    TotaisFechamento totais = new TotaisFechamento();
    totais.setTotalEntradas(new BigDecimal("600.00"));
    totais.setTotalAtivos(new BigDecimal("350.00"));
    totais.setTotalDebito(new BigDecimal("0.00"));
    totais.setTotalCredito(new BigDecimal("0.00"));
    totais.setTotalCartoes(new BigDecimal("0.00"));
    totais.setTotalDespesas(new BigDecimal("10.00"));
    totais.setTotalCaixa(new BigDecimal("590.00"));
    return totais;
  }

  private ConsistenciaResult createConsistenciaResult() {
    ConsistenciaResult result = new ConsistenciaResult();
    result.setConsistente(true);
    result.setDelta(BigDecimal.ZERO);
    return result;
  }
}
