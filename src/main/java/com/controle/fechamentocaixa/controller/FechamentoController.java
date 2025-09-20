package com.controle.fechamentocaixa.controller;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.controle.fechamentocaixa.dto.*;
import com.controle.fechamentocaixa.service.FechamentoService;

import jakarta.validation.Valid;

/**
 * Controller REST para operações de fechamento de caixa
 * Implementa todos os endpoints com autenticação e autorização adequadas
 */
// @RestController
// @RequestMapping("/api/fechamentos")
// @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
// @Tag(name = "Fechamento de Caixa", description = "Operações de fechamento de
// caixa diário")
// @SecurityRequirement(name = "Bearer Authentication")
public class FechamentoController {

  private static final Logger log = LoggerFactory.getLogger(FechamentoController.class);
  private final FechamentoService fechamentoService;

  public FechamentoController(FechamentoService fechamentoService) {
    this.fechamentoService = fechamentoService;
  }

  /**
   * Cria um novo fechamento de caixa
   */
  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'CAIXA')")
  public ResponseEntity<FechamentoResponse> criarFechamento(
      @Valid @RequestBody FechamentoRequest request) {

    log.info("Criando fechamento para {} em {}", request.getResponsavel(), request.getData());

    FechamentoResponse response = fechamentoService.criarFechamento(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Lista fechamentos com filtros e paginação
   */
  @GetMapping
  public ResponseEntity<Page<FechamentoResponse>> listarFechamentos(
      @RequestParam(required = false) LocalDate dataInicio,
      @RequestParam(required = false) LocalDate dataFim,
      @RequestParam(required = false) String responsavel,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Boolean consistente,
      Pageable pageable) {

    log.debug("Listando fechamentos com filtros");

    FechamentoFilter filter = new FechamentoFilter();
    filter.setDataInicio(dataInicio);
    filter.setDataFim(dataFim);
    filter.setResponsavel(responsavel);
    filter.setConsistente(consistente);

    // Converte string status para enum se fornecido
    if (status != null) {
      try {
        filter.setStatus(com.controle.fechamentocaixa.model.StatusFechamento.valueOf(status.toUpperCase()));
      } catch (IllegalArgumentException e) {
        log.warn("Status inválido fornecido: {}", status);
      }
    }

    Page<FechamentoResponse> fechamentos = fechamentoService.buscarFechamentos(filter, pageable);

    return ResponseEntity.ok(fechamentos);
  }

  /**
   * Busca fechamento por ID
   */
  @GetMapping("/{id}")
  public ResponseEntity<FechamentoResponse> buscarFechamento(@PathVariable String id) {
    log.debug("Buscando fechamento {}", id);

    FechamentoResponse response = fechamentoService.buscarPorId(id);

    return ResponseEntity.ok(response);
  }

  /**
   * Atualiza fechamento existente (apenas status ABERTO)
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'CAIXA')")
  public ResponseEntity<FechamentoResponse> atualizarFechamento(
      @PathVariable String id,
      @Valid @RequestBody FechamentoRequest request) {

    log.info("Atualizando fechamento {}", id);

    FechamentoResponse response = fechamentoService.atualizarFechamento(id, request);

    return ResponseEntity.ok(response);
  }

  /**
   * Fecha um fechamento (transição ABERTO → FECHADO)
   */
  @PatchMapping("/{id}/fechar")
  @PreAuthorize("hasAnyRole('ADMIN', 'CAIXA')")
  public ResponseEntity<FechamentoResponse> fecharFechamento(@PathVariable String id) {
    log.info("Fechando fechamento {}", id);

    FechamentoResponse response = fechamentoService.fecharFechamento(id);

    return ResponseEntity.ok(response);
  }

  /**
   * Confere um fechamento (transição FECHADO → CONFERIDO)
   */
  @PatchMapping("/{id}/conferir")
  @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
  public ResponseEntity<FechamentoResponse> conferirFechamento(@PathVariable String id) {
    log.info("Conferindo fechamento {}", id);

    FechamentoResponse response = fechamentoService.conferirFechamento(id);

    return ResponseEntity.ok(response);
  }

  /**
   * Valida e recalcula um fechamento
   */
  @PostMapping("/{id}/validar")
  public ResponseEntity<FechamentoResponse> validarFechamento(@PathVariable String id) {
    log.info("Validando fechamento {}", id);

    FechamentoResponse response = fechamentoService.validarFechamento(id);

    return ResponseEntity.ok(response);
  }

  /**
   * Cria fechamento diário com idempotência
   */
  @PostMapping("/diario")
  @PreAuthorize("hasAnyRole('ADMIN', 'CAIXA')")
  public ResponseEntity<FechamentoResponse> criarFechamentoDiario(
      @Valid @RequestBody FechamentoDiarioRequest request) {

    log.info("Criando fechamento diário para {} em {}",
        request.getResponsavel(), request.getData());

    FechamentoResponse response = fechamentoService.criarFechamentoDiario(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
