package com.controle.fechamentocaixa.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.controle.fechamentocaixa.dto.ErrorResponse;

/**
 * Handler global para tratamento de exceções
 * Padroniza respostas de erro em toda a aplicação
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private ErrorResponse createErrorResponse(HttpStatus status, String error, String message, String path,
      List<String> details) {
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setTimestamp(LocalDateTime.now());
    errorResponse.setStatus(status.value());
    errorResponse.setError(error);
    errorResponse.setMessage(message);
    errorResponse.setPath(path);
    errorResponse.setDetails(details);
    return errorResponse;
  }

  @ExceptionHandler(FechamentoNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleFechamentoNotFound(
      FechamentoNotFoundException ex, WebRequest request) {

    log.warn("Fechamento não encontrado: {}", ex.getMessage());

    ErrorResponse errorResponse = createErrorResponse(
        HttpStatus.NOT_FOUND,
        "Fechamento não encontrado",
        ex.getMessage(),
        getPath(request),
        null);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(FechamentoJaExisteException.class)
  public ResponseEntity<ErrorResponse> handleFechamentoJaExiste(
      FechamentoJaExisteException ex, WebRequest request) {

    log.warn("Fechamento já existe: {}", ex.getMessage());

    ErrorResponse errorResponse = createErrorResponse(
        HttpStatus.CONFLICT,
        "Fechamento já existe",
        ex.getMessage(),
        getPath(request),
        null);

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex, WebRequest request) {

    log.warn("Recurso não encontrado: {}", ex.getMessage());

    ErrorResponse errorResponse = createErrorResponse(
        HttpStatus.NOT_FOUND,
        "Recurso não encontrado",
        ex.getMessage(),
        getPath(request),
        null);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(
      ResourceAlreadyExistsException ex, WebRequest request) {

    log.warn("Recurso já existe: {}", ex.getMessage());

    ErrorResponse errorResponse = createErrorResponse(
        HttpStatus.CONFLICT,
        "Recurso já existe",
        ex.getMessage(),
        getPath(request),
        null);

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(StatusTransitionException.class)
  public ResponseEntity<ErrorResponse> handleStatusTransition(
      StatusTransitionException ex, WebRequest request) {

    log.warn("Transição de status inválida: {}", ex.getMessage());

    ErrorResponse errorResponse = createErrorResponse(
        HttpStatus.BAD_REQUEST,
        "Transição de status inválida",
        ex.getMessage(),
        getPath(request),
        null);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalState(
      IllegalStateException ex, WebRequest request) {

    log.warn("Estado inválido: {}", ex.getMessage());

    ErrorResponse errorResponse = createErrorResponse(
        HttpStatus.BAD_REQUEST,
        "Estado inválido",
        ex.getMessage(),
        getPath(request),
        null);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException ex, WebRequest request) {

    log.warn("Erro de validação: {}", ex.getMessage());

    List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.toList());

    ErrorResponse errorResponse = createErrorResponse(
        HttpStatus.BAD_REQUEST,
        "Erro de validação",
        "Dados inválidos fornecidos",
        getPath(request),
        errors);

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex, WebRequest request) {

    log.warn("Argumento inválido: {}", ex.getMessage());

    ErrorResponse errorResponse = createErrorResponse(
        HttpStatus.BAD_REQUEST,
        "Argumento inválido",
        ex.getMessage(),
        getPath(request),
        null);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex, WebRequest request) {

    log.error("Erro interno do servidor: {}", ex.getMessage(), ex);

    ErrorResponse errorResponse = createErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Erro interno do servidor",
        "Ocorreu um erro inesperado",
        getPath(request),
        null);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  private String getPath(WebRequest request) {
    return request.getDescription(false).replace("uri=", "");
  }
}
