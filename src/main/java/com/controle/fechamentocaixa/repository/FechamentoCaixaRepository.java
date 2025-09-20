package com.controle.fechamentocaixa.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.controle.fechamentocaixa.model.FechamentoCaixa;
import com.controle.fechamentocaixa.model.StatusFechamento;

/**
 * Repository para operações com FechamentoCaixa no MongoDB
 * Segue o design especificado com métodos para idempotência diária e consultas
 * otimizadas
 */
@Repository
public interface FechamentoCaixaRepository extends MongoRepository<FechamentoCaixa, String> {

  /**
   * Busca fechamento por responsável e data (para idempotência diária)
   * Usado para prevenir fechamentos duplicados no mesmo dia
   */
  Optional<FechamentoCaixa> findByResponsavelAndData(String responsavel, LocalDate data);

  /**
   * Busca fechamentos por intervalo de datas ordenados por data decrescente
   * Usado para consultas de período com paginação
   */
  List<FechamentoCaixa> findByDataBetweenOrderByDataDesc(LocalDate dataInicio, LocalDate dataFim);

  /**
   * Busca fechamentos por intervalo de datas com paginação
   */
  Page<FechamentoCaixa> findByDataBetweenOrderByDataDesc(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

  /**
   * Busca fechamentos por status e intervalo de datas
   */
  List<FechamentoCaixa> findByStatusAndDataBetween(StatusFechamento status, LocalDate dataInicio, LocalDate dataFim);

  /**
   * Busca fechamentos por responsável ordenados por data decrescente
   */
  List<FechamentoCaixa> findByResponsavelOrderByDataDesc(String responsavel);

  /**
   * Busca fechamentos por status ordenados por data decrescente
   */
  List<FechamentoCaixa> findByStatusOrderByDataDesc(StatusFechamento status);

  /**
   * Busca fechamentos inconsistentes (com diferença)
   */
  @Query("{ 'consistente': false }")
  List<FechamentoCaixa> findFechamentosInconsistentes();

  /**
   * Busca fechamentos por data específica
   */
  List<FechamentoCaixa> findByDataOrderByResponsavel(LocalDate data);

  /**
   * Conta fechamentos por status
   */
  long countByStatus(StatusFechamento status);

  /**
   * Busca último fechamento de um responsável
   */
  Optional<FechamentoCaixa> findFirstByResponsavelOrderByDataDesc(String responsavel);

  /**
   * Busca fechamentos que precisam de conferência (status FECHADO)
   */
  List<FechamentoCaixa> findByStatusOrderByCriadoEmAsc(StatusFechamento status);

  /**
   * Verifica se existe fechamento para responsável e data
   */
  boolean existsByResponsavelAndData(String responsavel, LocalDate data);

  /**
   * Busca fechamentos criados em um período específico
   */
  List<FechamentoCaixa> findByCriadoEmBetweenOrderByCriadoEmDesc(LocalDateTime inicio, LocalDateTime fim);
}
