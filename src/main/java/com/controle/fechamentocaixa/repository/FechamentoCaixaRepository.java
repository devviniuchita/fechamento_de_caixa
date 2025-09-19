package com.controle.fechamentocaixa.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.controle.fechamentocaixa.model.FechamentoCaixa;
import com.controle.fechamentocaixa.model.StatusFechamento;

/**
 * Repository para operações com FechamentoCaixa no MongoDB Atlas
 */
@Repository
public interface FechamentoCaixaRepository extends MongoRepository<FechamentoCaixa, String> {

  /**
   * Busca fechamento por caixa
   */
  Optional<FechamentoCaixa> findByCaixaId(String caixaId);

  /**
   * Busca fechamentos por status
   */
  List<FechamentoCaixa> findByStatusOrderByDataFechamentoDesc(StatusFechamento status);

  /**
   * Busca fechamentos por período
   */
  List<FechamentoCaixa> findByDataFechamentoGreaterThanEqualAndDataFechamentoLessThanEqual(
      LocalDateTime inicio, LocalDateTime fim);

  /**
   * Busca fechamentos de um usuário
   */
  List<FechamentoCaixa> findByUsuarioIdOrderByDataFechamentoDesc(String usuarioId);

  /**
   * Busca fechamentos com diferença
   */
  @Query("{ 'diferencaTotal': { $ne: 0 } }")
  List<FechamentoCaixa> findFechamentosComDiferenca();

  /**
   * Busca fechamentos pendentes de aprovação
   */
  List<FechamentoCaixa> findByStatusAndDataFechamentoLessThan(
      StatusFechamento status, LocalDateTime dataLimite);

  /**
   * Conta fechamentos por status
   */
  long countByStatus(StatusFechamento status);

  /**
   * Busca último fechamento de um usuário
   */
  Optional<FechamentoCaixa> findFirstByUsuarioIdOrderByDataFechamentoDesc(String usuarioId);

  /**
   * Busca fechamentos para auditoria
   */
  @Query("{ $or: [ " +
      "{ 'diferencaTotal': { $gt: 0 } }, " +
      "{ 'status': 'EM_AUDITORIA' } " +
      "] }")
  List<FechamentoCaixa> findFechamentosParaAuditoria();
}
