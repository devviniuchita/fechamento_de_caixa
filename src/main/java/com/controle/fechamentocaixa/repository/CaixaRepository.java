package com.controle.fechamentocaixa.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.controle.fechamentocaixa.model.Caixa;
import com.controle.fechamentocaixa.model.StatusCaixa;

/**
 * Repository para operações com Caixa no MongoDB Atlas
 */
@Repository
public interface CaixaRepository extends MongoRepository<Caixa, String> {

  /**
   * Busca caixa aberto por usuário
   */
  Optional<Caixa> findByUsuarioIdAndStatus(String usuarioId, StatusCaixa status);

  /**
   * Busca todos os caixas em determinado status
   */
  List<Caixa> findByStatus(StatusCaixa status);

  /**
   * Busca caixas por período
   */
  List<Caixa> findByDataAberturaGreaterThanEqualAndDataAberturaLessThanEqual(
      LocalDateTime inicio, LocalDateTime fim);

  /**
   * Busca caixas de um usuário específico
   */
  List<Caixa> findByUsuarioIdOrderByDataAberturaDesc(String usuarioId);

  /**
   * Verifica se usuário tem caixa aberto
   */
  boolean existsByUsuarioIdAndStatus(String usuarioId, StatusCaixa status);

  /**
   * Busca caixas não fechados há mais de X horas
   */
  @Query("{ 'status': ?0, 'dataAbertura': { $lt: ?1 } }")
  List<Caixa> findCaixasAbertosMuitoTempo(StatusCaixa status, LocalDateTime dataLimite);

  /**
   * Conta caixas por status
   */
  long countByStatus(StatusCaixa status);

  /**
   * Busca último caixa de um usuário
   */
  Optional<Caixa> findFirstByUsuarioIdOrderByDataAberturaDesc(String usuarioId);
}
