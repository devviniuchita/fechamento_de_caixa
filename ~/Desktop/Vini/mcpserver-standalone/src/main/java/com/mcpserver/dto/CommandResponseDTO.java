package com.mcpserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO para representar uma resposta a um comando do MCP Server
 * Utilizado para padronizar a comunicação entre servidor e cliente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandResponseDTO {
    
    /**
     * Indica se o comando foi executado com sucesso
     */
    private boolean success;
    
    /**
     * Resultados da execução do comando
     */
    private Map<String, Object> results = new HashMap<>();
    
    /**
     * Mensagem de erro, caso ocorra
     */
    private String errorMessage;
    
    /**
     * Identificador único da solicitação correspondente
     */
    private String requestId;
    
    /**
     * Timestamp da resposta
     */
    private long timestamp = System.currentTimeMillis();
    
    /**
     * Tempo de execução em milissegundos
     */
    private long executionTimeMs;
} 