package com.controle.fechamentocaixa.exception;

/**
 * Exceção lançada quando tenta criar um fechamento que já existe (violação de
 * idempotência)
 */
public class FechamentoJaExisteException extends RuntimeException {

  public FechamentoJaExisteException(String message) {
    super(message);
  }

  public FechamentoJaExisteException(String message, Throwable cause) {
    super(message, cause);
  }
}
