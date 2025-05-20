package com.mcpserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO para representar uma solicitação de comando ao MCP Server
 * Utilizado para padronizar a comunicação entre cliente e servidor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandRequestDTO {
    
    /**
     * Nome do comando a ser executado
     */
    private String command;
    
    /**
     * Parâmetros associados ao comando
     */
    private Map<String, Object> parameters = new HashMap<>();
    
    /**
     * Identificador único da solicitação
     */
    private String requestId;
    
    /**
     * Timestamp da solicitação
     */
    private long timestamp = System.currentTimeMillis();
} 