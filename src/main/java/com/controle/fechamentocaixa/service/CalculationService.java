package com.controle.fechamentocaixa.service;

import java.math.BigDecimal;
import java.util.List;

import com.controle.fechamentocaixa.dto.*;
import com.controle.fechamentocaixa.model.*;

/**
 * Interface para serviço de cálculos financeiros do fechamento de caixa
 * Implementa todas as regras de negócio para cálculos com precisão BigDecimal
 */
public interface CalculationService {

  /**
   * Calcula todos os totais do fechamento de caixa
   */
  TotaisFechamento calcularTotais(FechamentoRequest request);

  /**
   * Verifica consistência do fechamento com margem de tolerância
   */
  ConsistenciaResult verificarConsistencia(TotaisFechamento totais);

  /**
   * Calcula total de entradas: CaixaInicial + Vendas + TrocoInserido
   */
  BigDecimal calcularTotalEntradas(BigDecimal caixaInicial, BigDecimal vendas, BigDecimal trocoInserido);

  /**
   * Calcula total de ativos: Dinheiro + PIX + Deposito + Vale + Sangria
   */
  BigDecimal calcularTotalAtivos(FormasPagamento formas);

  /**
   * Calcula total de cartões: TotalDebito + TotalCredito + Voucher
   */
  BigDecimal calcularTotalCartoes(CartaoDebito debito, CartaoCredito credito, BigDecimal voucher);

  /**
   * Calcula total de despesas
   */
  BigDecimal calcularTotalDespesas(List<Despesa> despesas);

  /**
   * Calcula total de débito por bandeira
   */
  BigDecimal calcularTotalDebito(CartaoDebito debito);

  /**
   * Calcula total de crédito por bandeira
   */
  BigDecimal calcularTotalCredito(CartaoCredito credito);
}
