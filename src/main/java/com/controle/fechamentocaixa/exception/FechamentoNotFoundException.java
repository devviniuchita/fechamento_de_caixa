package com.controle.fechamentocaixa.exception;

/**
 * Exceção lançada quando um fechamento de caixa não é encontrado
 */
public class FechamentoNotFoundException extends RuntimeException {

  public FechamentoNotFoundException(String message) {
    super(message);
  }

  public FechamentoNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
