package com.controle.fechamentocaixa.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.controle.fechamentocaixa.dto.*;

/**
 * Interface para serviço de negócio de fechamento de caixa
 * Implementa todas as regras de negócio e validações
 */
public interface FechamentoService {

  /**
   * Cria um novo fechamento de caixa
   */
  FechamentoResponse criarFechamento(FechamentoRequest request);

  /**
   * Atualiza um fechamento de caixa existente (apenas se status ABERTO)
   */
  FechamentoResponse atualizarFechamento(String id, FechamentoRequest request);

  /**
   * Busca fechamento por ID
   */
  FechamentoResponse buscarPorId(String id);

  /**
   * Busca fechamentos com filtros e paginação
   */
  Page<FechamentoResponse> buscarFechamentos(FechamentoFilter filter, Pageable pageable);

  /**
   * Fecha um fechamento (transição ABERTO → FECHADO)
   */
  FechamentoResponse fecharFechamento(String id);

  /**
   * Confere um fechamento (transição FECHADO → CONFERIDO)
   */
  FechamentoResponse conferirFechamento(String id);

  /**
   * Valida e recalcula um fechamento
   */
  FechamentoResponse validarFechamento(String id);

  /**
   * Cria fechamento diário com validação de idempotência
   */
  FechamentoResponse criarFechamentoDiario(String responsavel, LocalDate data);

  /**
   * Cria fechamento diário usando DTO
   */
  FechamentoResponse criarFechamentoDiario(FechamentoDiarioRequest request);
}
