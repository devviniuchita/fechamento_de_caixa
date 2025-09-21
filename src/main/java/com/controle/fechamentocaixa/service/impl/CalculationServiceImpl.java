package com.controle.fechamentocaixa.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.controle.fechamentocaixa.dto.*;
import com.controle.fechamentocaixa.model.*;
import com.controle.fechamentocaixa.service.CalculationService;

/**
 * Implementação do serviço de cálculos financeiros
 * Usa precisão BigDecimal com 2 casas decimais para garantir exatidão monetária
 */
@Service
public class CalculationServiceImpl implements CalculationService {

  private static final Logger log = LoggerFactory.getLogger(CalculationServiceImpl.class);
  private static final BigDecimal TOLERANCIA_CONSISTENCIA = new BigDecimal("0.01");
  private static final int ESCALA_MONETARIA = 2;

  @Override
  public TotaisFechamento calcularTotais(FechamentoRequest request) {
    log.debug("Calculando totais para fechamento de {}", request.getResponsavel());

    // Calcula total de entradas
    BigDecimal totalEntradas = calcularTotalEntradas(
        request.getCaixaInicial(),
        request.getVendas(),
        request.getTrocoInserido());

    // Calcula total de ativos
    BigDecimal totalAtivos = calcularTotalAtivos(request.getFormasPagamento());

    // Calcula totais de cartões
    BigDecimal totalDebito = BigDecimal.ZERO;
    BigDecimal totalCredito = BigDecimal.ZERO;
    BigDecimal totalCartoes = BigDecimal.ZERO;

    if (request.getFormasPagamento() != null) {
      totalDebito = calcularTotalDebito(request.getFormasPagamento().getDebito());
      totalCredito = calcularTotalCredito(request.getFormasPagamento().getCredito());

      BigDecimal voucher = request.getFormasPagamento().getVoucher() != null
          ? request.getFormasPagamento().getVoucher()
          : BigDecimal.ZERO;

      totalCartoes = calcularTotalCartoes(
          request.getFormasPagamento().getDebito(),
          request.getFormasPagamento().getCredito(),
          voucher);
    }

    // Calcula total de despesas
    BigDecimal totalDespesas = calcularTotalDespesas(request.getDespesas());

    // Calcula total do caixa: TotalAtivos + TotalCartoes + TotalDespesas -
    // TotalEntradas
    BigDecimal totalCaixa = totalAtivos
        .add(totalCartoes)
        .add(totalDespesas)
        .subtract(totalEntradas)
        .setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);

    TotaisFechamento totais = new TotaisFechamento();
    totais.setTotalEntradas(totalEntradas);
    totais.setTotalAtivos(totalAtivos);
    totais.setTotalDebito(totalDebito);
    totais.setTotalCredito(totalCredito);
    totais.setTotalCartoes(totalCartoes);
    totais.setTotalDespesas(totalDespesas);
    totais.setTotalCaixa(totalCaixa);
    return totais;
  }

  @Override
  public ConsistenciaResult verificarConsistencia(TotaisFechamento totais) {
    BigDecimal delta = totais.getTotalCaixa();
    BigDecimal deltaAbsoluto = delta.abs();

    boolean consistente = deltaAbsoluto.compareTo(TOLERANCIA_CONSISTENCIA) <= 0;

    String mensagem = consistente
        ? "Fechamento consistente"
        : String.format("Diferença de R$ %s encontrada", delta);

    log.debug("Verificação de consistência: {} (delta: {})",
        consistente ? "CONSISTENTE" : "INCONSISTENTE", delta);

    ConsistenciaResult result = new ConsistenciaResult();
    result.setConsistente(consistente);
    result.setDelta(delta);
    result.setTolerancia(TOLERANCIA_CONSISTENCIA);
    result.setMensagem(mensagem);
    return result;
  }

  @Override
  public BigDecimal calcularTotalEntradas(BigDecimal caixaInicial, BigDecimal vendas, BigDecimal trocoInserido) {
    BigDecimal total = safeAdd(caixaInicial, vendas, trocoInserido);
    log.debug("Total entradas: {} (Caixa: {}, Vendas: {}, Troco: {})",
        total, caixaInicial, vendas, trocoInserido);
    return total;
  }

  @Override
  public BigDecimal calcularTotalAtivos(FormasPagamento formas) {
    if (formas == null) {
      return BigDecimal.ZERO.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
    }

    BigDecimal total = safeAdd(
        formas.getDinheiro(),
        formas.getPix(),
        formas.getDeposito(),
        formas.getVale(),
        formas.getSangria());

    log.debug("Total ativos: {} (Dinheiro: {}, PIX: {}, Depósito: {}, Vale: {}, Sangria: {})",
        total, formas.getDinheiro(), formas.getPix(), formas.getDeposito(),
        formas.getVale(), formas.getSangria());

    return total;
  }

  @Override
  public BigDecimal calcularTotalCartoes(CartaoDebito debito, CartaoCredito credito, BigDecimal voucher) {
    BigDecimal totalDebito = calcularTotalDebito(debito);
    BigDecimal totalCredito = calcularTotalCredito(credito);
    BigDecimal voucherSafe = voucher != null ? voucher : BigDecimal.ZERO;

    BigDecimal total = totalDebito
        .add(totalCredito)
        .add(voucherSafe)
        .setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);

    log.debug("Total cartões: {} (Débito: {}, Crédito: {}, Voucher: {})",
        total, totalDebito, totalCredito, voucherSafe);

    return total;
  }

  @Override
  public BigDecimal calcularTotalDespesas(List<Despesa> despesas) {
    if (despesas == null || despesas.isEmpty()) {
      return BigDecimal.ZERO.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
    }

    BigDecimal total = despesas.stream()
        .map(Despesa::getValor)
        .filter(valor -> valor != null)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);

    log.debug("Total despesas: {} ({} itens)", total, despesas.size());
    return total;
  }

  @Override
  public BigDecimal calcularTotalDebito(CartaoDebito debito) {
    if (debito == null) {
      return BigDecimal.ZERO.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
    }

    BigDecimal total = safeAdd(debito.getVisa(), debito.getMaster(), debito.getElo());
    log.debug("Total débito: {} (Visa: {}, Master: {}, Elo: {})",
        total, debito.getVisa(), debito.getMaster(), debito.getElo());

    return total;
  }

  @Override
  public BigDecimal calcularTotalCredito(CartaoCredito credito) {
    if (credito == null) {
      return BigDecimal.ZERO.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
    }

    BigDecimal total = safeAdd(credito.getVisa(), credito.getMaster(), credito.getElo());
    log.debug("Total crédito: {} (Visa: {}, Master: {}, Elo: {})",
        total, credito.getVisa(), credito.getMaster(), credito.getElo());

    return total;
  }

  /**
   * Soma valores BigDecimal de forma segura, tratando valores nulos como zero
   */
  private BigDecimal safeAdd(BigDecimal... valores) {
    BigDecimal total = BigDecimal.ZERO;

    for (BigDecimal valor : valores) {
      if (valor != null) {
        total = total.add(valor);
      }
    }

    return total.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
  }
}
