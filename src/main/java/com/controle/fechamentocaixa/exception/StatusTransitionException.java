package com.controle.fechamentocaixa.exception;

/**
 * Exceção lançada quando uma transição de status inválida é tentada
 */
public class StatusTransitionException extends RuntimeException {

  public StatusTransitionException(String message) {
    super(message);
  }

  public StatusTransitionException(String message, Throwable cause) {
    super(message, cause);
  }
}
