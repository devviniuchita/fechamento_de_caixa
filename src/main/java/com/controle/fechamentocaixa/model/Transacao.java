package com.controle.fechamentocaixa.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

/**
 * Entity Transacao - Representa uma transação/venda realizada no caixa
 * Conecta com MongoDB Atlas Azure East US 2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transacoes")
public class Transacao {

  @Id
  private String id;

  @Indexed
  private String caixaId; // ID do caixa onde foi realizada

  @Indexed
  private String usuarioId; // ID do usuário que realizou

  private String nomeUsuario;

  @Indexed
  private LocalDateTime dataTransacao;

  private BigDecimal valor;

  @Indexed
  private TipoTransacao tipo;

  @Indexed
  private FormaPagamento formaPagamento;

  private String descricao;

  private String numeroDocumento; // Número da venda, cupom fiscal, etc.

  private String cliente; // Nome do cliente (opcional)

  // Campos específicos para diferentes formas de pagamento
  private String numeroCartao; // 4 últimos dígitos
  private String bandeira; // Visa, Master, etc.
  private String numeroAutorizacao;
  private String chavePix;
  private String numeroCheque;
  private String banco;

  // Controles de estorno
  private Boolean estornada;
  private String transacaoEstornoId;
  private String motivoEstorno;
  private LocalDateTime dataEstorno;

  // Auditoria
  private LocalDateTime dataCriacao;
  private LocalDateTime dataUltimaAtualizacao;
  private String criadoPor;
  private String atualizadoPor;

  // Métodos de conveniência
  public boolean isVenda() {
    return tipo == TipoTransacao.VENDA;
  }

  public boolean isEstorno() {
    return tipo == TipoTransacao.ESTORNO;
  }

  public boolean isDinheiro() {
    return formaPagamento == FormaPagamento.DINHEIRO;
  }

  public boolean isCartao() {
    return formaPagamento == FormaPagamento.CARTAO_CREDITO ||
        formaPagamento == FormaPagamento.CARTAO_DEBITO;
  }
}
