package com.controle.fechamentocaixa.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.controle.fechamentocaixa.model.*;

/**
 * Repository para operações com Transacao no MongoDB Atlas
 */
@Repository
public interface TransacaoRepository extends MongoRepository<Transacao, String> {

  /**
   * Busca transações de um caixa específico
   */
  List<Transacao> findByCaixaIdOrderByDataTransacaoDesc(String caixaId);

  /**
   * Busca transações por tipo
   */
  List<Transacao> findByTipoAndDataTransacaoGreaterThanEqualAndDataTransacaoLessThanEqual(
      TipoTransacao tipo, LocalDateTime inicio, LocalDateTime fim);

  /**
   * Busca transações por forma de pagamento
   */
  List<Transacao> findByFormaPagamentoAndDataTransacaoGreaterThanEqualAndDataTransacaoLessThanEqual(
      FormaPagamento formaPagamento, LocalDateTime inicio, LocalDateTime fim);

  /**
   * Soma total de vendas por caixa
   */
  @Query(value = "{ 'caixaId': ?0, 'tipo': 'VENDA', 'estornada': { $ne: true } }", count = false)
  List<Transacao> findVendasByCaixaId(String caixaId);

  /**
   * Soma total por forma de pagamento e caixa
   */
  @Query("{ 'caixaId': ?0, 'formaPagamento': ?1, 'tipo': 'VENDA', 'estornada': { $ne: true } }")
  List<Transacao> findByCaixaIdAndFormaPagamentoAndVenda(String caixaId, FormaPagamento formaPagamento);

  /**
   * Busca estornos de um caixa
   */
  List<Transacao> findByCaixaIdAndTipo(String caixaId, TipoTransacao tipo);

  /**
   * Busca transações por usuário
   */
  List<Transacao> findByUsuarioIdOrderByDataTransacaoDesc(String usuarioId);

  /**
   * Busca transações não estornadas
   */
  @Query("{ 'estornada': { $ne: true } }")
  List<Transacao> findTransacoesAtivas();

  /**
   * Conta transações por tipo em período
   */
  long countByTipoAndDataTransacaoGreaterThanEqualAndDataTransacaoLessThanEqual(
      TipoTransacao tipo, LocalDateTime inicio, LocalDateTime fim);

  /**
   * Busca últimas transações
   */
  List<Transacao> findTop10ByOrderByDataTransacaoDesc();
}
