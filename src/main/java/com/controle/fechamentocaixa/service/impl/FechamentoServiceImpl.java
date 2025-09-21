package com.controle.fechamentocaixa.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.controle.fechamentocaixa.dto.*;
import com.controle.fechamentocaixa.exception.ResourceAlreadyExistsException;
import com.controle.fechamentocaixa.exception.ResourceNotFoundException;
import com.controle.fechamentocaixa.model.FechamentoCaixa;
import com.controle.fechamentocaixa.model.StatusFechamento;
import com.controle.fechamentocaixa.repository.FechamentoCaixaRepository;
import com.controle.fechamentocaixa.service.CalculationService;
import com.controle.fechamentocaixa.service.FechamentoService;

/**
 * Implementação do serviço de negócio de fechamento de caixa
 * Gerencia transações, validações e regras de negócio
 */
@Service
@Transactional
public class FechamentoServiceImpl implements FechamentoService {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FechamentoServiceImpl.class);

  private final FechamentoCaixaRepository repository;
  private final CalculationService calculationService;

  // Constructor manual para compatibilidade com Java 24
  public FechamentoServiceImpl(FechamentoCaixaRepository repository, CalculationService calculationService) {
    this.repository = repository;
    this.calculationService = calculationService;
  }

  @Override
  public FechamentoResponse criarFechamento(FechamentoRequest request) {
    log.info("Criando fechamento para {} em {}", request.getResponsavel(), request.getData());

    // Verifica idempotência diária
    if (repository.existsByResponsavelAndData(request.getResponsavel(), request.getData())) {
      throw new ResourceAlreadyExistsException(
          String.format("Já existe fechamento para %s na data %s",
              request.getResponsavel(), request.getData()));
    }

    // Calcula totais
    TotaisFechamento totais = calculationService.calcularTotais(request);
    ConsistenciaResult consistencia = calculationService.verificarConsistencia(totais);

    // Cria entidade
    FechamentoCaixa fechamento = mapToEntity(request);
    aplicarTotais(fechamento, totais);
    aplicarConsistencia(fechamento, consistencia);

    // Define campos de auditoria
    String usuarioAtual = getCurrentUser();
    fechamento.setCriadoEm(LocalDateTime.now());
    fechamento.setCriadoPor(usuarioAtual);
    fechamento.setAtualizadoEm(LocalDateTime.now());
    fechamento.setAtualizadoPor(usuarioAtual);

    // Salva
    FechamentoCaixa saved = repository.save(fechamento);

    log.info("Fechamento criado com ID: {}", saved.getId());
    return mapToResponse(saved);
  }

  @Override
  public FechamentoResponse atualizarFechamento(String id, FechamentoRequest request) {
    log.info("Atualizando fechamento {}", id);

    FechamentoCaixa fechamento = findById(id);

    // Só permite atualização se status ABERTO
    if (fechamento.getStatus() != StatusFechamento.ABERTO) {
      throw new IllegalStateException("Só é possível atualizar fechamentos com status ABERTO");
    }

    // Recalcula totais
    TotaisFechamento totais = calculationService.calcularTotais(request);
    ConsistenciaResult consistencia = calculationService.verificarConsistencia(totais);

    // Atualiza dados
    updateEntityFromRequest(fechamento, request);
    aplicarTotais(fechamento, totais);
    aplicarConsistencia(fechamento, consistencia);

    // Atualiza auditoria
    fechamento.setAtualizadoEm(LocalDateTime.now());
    fechamento.setAtualizadoPor(getCurrentUser());

    FechamentoCaixa updated = repository.save(fechamento);

    log.info("Fechamento {} atualizado", id);
    return mapToResponse(updated);
  }

  @Override
  @Transactional(readOnly = true)
  public FechamentoResponse buscarPorId(String id) {
    FechamentoCaixa fechamento = findById(id);
    return mapToResponse(fechamento);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<FechamentoResponse> buscarFechamentos(FechamentoFilter filter, Pageable pageable) {
    log.debug("Buscando fechamentos com filtros: {}", filter);

    List<FechamentoCaixa> fechamentos;

    if (filter.getDataInicio() != null && filter.getDataFim() != null) {
      fechamentos = repository.findByDataBetweenOrderByDataDesc(
          filter.getDataInicio(), filter.getDataFim(), pageable).getContent();
    } else {
      fechamentos = repository.findAll(pageable).getContent();
    }

    // Aplica filtros adicionais
    if (filter.getResponsavel() != null) {
      fechamentos = fechamentos.stream()
          .filter(f -> f.getResponsavel().equals(filter.getResponsavel()))
          .collect(Collectors.toList());
    }

    if (filter.getStatus() != null) {
      fechamentos = fechamentos.stream()
          .filter(f -> f.getStatus() == filter.getStatus())
          .collect(Collectors.toList());
    }

    if (filter.getConsistente() != null) {
      fechamentos = fechamentos.stream()
          .filter(f -> f.getConsistente().equals(filter.getConsistente()))
          .collect(Collectors.toList());
    }

    List<FechamentoResponse> responses = fechamentos.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());

    return new PageImpl<>(responses, pageable, responses.size());
  }

  @Override
  @PreAuthorize("hasAnyRole('ADMIN', 'CAIXA')")
  public FechamentoResponse fecharFechamento(String id) {
    log.info("Fechando fechamento {}", id);

    FechamentoCaixa fechamento = findById(id);

    // Valida transição de status
    if (fechamento.getStatus() != StatusFechamento.ABERTO) {
      throw new IllegalStateException("Só é possível fechar fechamentos com status ABERTO");
    }

    fechamento.setStatus(StatusFechamento.FECHADO);
    fechamento.setAtualizadoEm(LocalDateTime.now());
    fechamento.setAtualizadoPor(getCurrentUser());

    FechamentoCaixa updated = repository.save(fechamento);

    log.info("Fechamento {} fechado", id);
    return mapToResponse(updated);
  }

  @Override
  @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
  public FechamentoResponse conferirFechamento(String id) {
    log.info("Conferindo fechamento {}", id);

    FechamentoCaixa fechamento = findById(id);

    // Valida transição de status
    if (fechamento.getStatus() != StatusFechamento.FECHADO) {
      throw new IllegalStateException("Só é possível conferir fechamentos com status FECHADO");
    }

    fechamento.setStatus(StatusFechamento.CONFERIDO);
    fechamento.setAtualizadoEm(LocalDateTime.now());
    fechamento.setAtualizadoPor(getCurrentUser());

    FechamentoCaixa updated = repository.save(fechamento);

    log.info("Fechamento {} conferido", id);
    return mapToResponse(updated);
  }

  @Override
  public FechamentoResponse validarFechamento(String id) {
    log.info("Validando fechamento {}", id);

    FechamentoCaixa fechamento = findById(id);

    // Recria request a partir da entidade para recalcular
    FechamentoRequest request = mapToRequest(fechamento);

    // Recalcula totais
    TotaisFechamento totais = calculationService.calcularTotais(request);
    ConsistenciaResult consistencia = calculationService.verificarConsistencia(totais);

    // Aplica novos cálculos
    aplicarTotais(fechamento, totais);
    aplicarConsistencia(fechamento, consistencia);

    fechamento.setAtualizadoEm(LocalDateTime.now());
    fechamento.setAtualizadoPor(getCurrentUser());

    FechamentoCaixa updated = repository.save(fechamento);

    log.info("Fechamento {} validado", id);
    return mapToResponse(updated);
  }

  @Override
  public FechamentoResponse criarFechamentoDiario(String responsavel, LocalDate data) {
    FechamentoDiarioRequest request = FechamentoDiarioRequest.builder()
        .responsavel(responsavel)
        .data(data)
        .build();

    return criarFechamentoDiario(request);
  }

  @Override
  public FechamentoResponse criarFechamentoDiario(FechamentoDiarioRequest request) {
    log.info("Criando fechamento diário para {} em {}",
        request.getResponsavel(), request.getData());

    // Verifica idempotência
    if (repository.existsByResponsavelAndData(request.getResponsavel(), request.getData())) {
      throw new ResourceAlreadyExistsException(
          String.format("Já existe fechamento para %s na data %s",
              request.getResponsavel(), request.getData()));
    }

    // Cria fechamento básico
    FechamentoRequest fechamentoRequest = FechamentoRequest.builder()
        .data(request.getData())
        .responsavel(request.getResponsavel())
        .build();

    return criarFechamento(fechamentoRequest);
  }

  // Métodos auxiliares

  private FechamentoCaixa findById(String id) {
    return repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Fechamento não encontrado: " + id));
  }

  private String getCurrentUser() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  private FechamentoCaixa mapToEntity(FechamentoRequest request) {
    return FechamentoCaixa.builder()
        .data(request.getData())
        .responsavel(request.getResponsavel())
        .caixaInicial(request.getCaixaInicial())
        .vendas(request.getVendas())
        .trocoInserido(request.getTrocoInserido())
        .formasPagamento(request.getFormasPagamento())
        .despesas(request.getDespesas())
        .observacoes(request.getObservacoes())
        .comprovantes(request.getComprovantes())
        .status(StatusFechamento.ABERTO)
        .build();
  }

  private void updateEntityFromRequest(FechamentoCaixa entity, FechamentoRequest request) {
    entity.setCaixaInicial(request.getCaixaInicial());
    entity.setVendas(request.getVendas());
    entity.setTrocoInserido(request.getTrocoInserido());
    entity.setFormasPagamento(request.getFormasPagamento());
    entity.setDespesas(request.getDespesas());
    entity.setObservacoes(request.getObservacoes());
    entity.setComprovantes(request.getComprovantes());
  }

  private void aplicarTotais(FechamentoCaixa entity, TotaisFechamento totais) {
    entity.setTotalEntradas(totais.getTotalEntradas());
    entity.setTotalAtivos(totais.getTotalAtivos());
    entity.setTotalDebito(totais.getTotalDebito());
    entity.setTotalCredito(totais.getTotalCredito());
    entity.setTotalCartoes(totais.getTotalCartoes());
    entity.setTotalDespesas(totais.getTotalDespesas());
    entity.setTotalCaixa(totais.getTotalCaixa());
  }

  private void aplicarConsistencia(FechamentoCaixa entity, ConsistenciaResult consistencia) {
    entity.setConsistente(consistencia.getConsistente());
    entity.setDelta(consistencia.getDelta());
  }

  private FechamentoResponse mapToResponse(FechamentoCaixa entity) {
    return FechamentoResponse.builder()
        .id(entity.getId())
        .data(entity.getData())
        .responsavel(entity.getResponsavel())
        .caixaInicial(entity.getCaixaInicial())
        .vendas(entity.getVendas())
        .trocoInserido(entity.getTrocoInserido())
        .formasPagamento(entity.getFormasPagamento())
        .despesas(entity.getDespesas())
        .totalEntradas(entity.getTotalEntradas())
        .totalAtivos(entity.getTotalAtivos())
        .totalDebito(entity.getTotalDebito())
        .totalCredito(entity.getTotalCredito())
        .totalCartoes(entity.getTotalCartoes())
        .totalDespesas(entity.getTotalDespesas())
        .totalCaixa(entity.getTotalCaixa())
        .consistente(entity.getConsistente())
        .delta(entity.getDelta())
        .status(entity.getStatus())
        .observacoes(entity.getObservacoes())
        .comprovantes(entity.getComprovantes())
        .criadoEm(entity.getCriadoEm())
        .atualizadoEm(entity.getAtualizadoEm())
        .criadoPor(entity.getCriadoPor())
        .atualizadoPor(entity.getAtualizadoPor())
        .build();
  }

  private FechamentoRequest mapToRequest(FechamentoCaixa entity) {
    return FechamentoRequest.builder()
        .data(entity.getData())
        .responsavel(entity.getResponsavel())
        .caixaInicial(entity.getCaixaInicial())
        .vendas(entity.getVendas())
        .trocoInserido(entity.getTrocoInserido())
        .formasPagamento(entity.getFormasPagamento())
        .despesas(entity.getDespesas())
        .observacoes(entity.getObservacoes())
        .comprovantes(entity.getComprovantes())
        .build();
  }
}
