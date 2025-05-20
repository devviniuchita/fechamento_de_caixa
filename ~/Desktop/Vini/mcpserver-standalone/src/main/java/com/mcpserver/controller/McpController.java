package com.mcpserver.controller;

import com.mcpserver.dto.CommandRequestDTO;
import com.mcpserver.dto.CommandResponseDTO;
import com.mcpserver.interfaces.McpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para exposição dos endpoints MCP
 * Implementa a API REST utilizada pelo Cursor IDE
 */
@RestController
@RequestMapping("/api/mcp")
@CrossOrigin(origins = "*")
public class McpController {

    private static final Logger logger = LoggerFactory.getLogger(McpController.class);
    
    private final McpService mcpService;
    
    @Autowired
    public McpController(McpService mcpService) {
        this.mcpService = mcpService;
    }
    
    /**
     * Endpoint para verificação de status do servidor
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        logger.info("Requisição de status recebida");
        return ResponseEntity.ok(mcpService.getStatus());
    }
    
    /**
     * Endpoint para execução de comandos
     */
    @PostMapping("/execute")
    public ResponseEntity<CommandResponseDTO> executeCommand(@RequestBody CommandRequestDTO request) {
        logger.info("Requisição de execução recebida: {}", request);
        
        long startTime = System.currentTimeMillis();
        
        Map<String, Object> result = mcpService.executeCommand(
                request.getCommand(), 
                request.getParameters()
        );
        
        long executionTime = System.currentTimeMillis() - startTime;
        
        CommandResponseDTO response = CommandResponseDTO.builder()
                .success(result.containsKey("success") ? (Boolean) result.get("success") : true)
                .results(result)
                .requestId(request.getRequestId())
                .timestamp(System.currentTimeMillis())
                .executionTimeMs(executionTime)
                .build();
        
        if (result.containsKey("error")) {
            response.setErrorMessage((String) result.get("error"));
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint para verificação de saúde do servidor
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        logger.info("Requisição de health check recebida");
        
        boolean isHealthy = mcpService.healthCheck();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", isHealthy ? "UP" : "DOWN");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint para sincronização de dados
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncData(@RequestBody Map<String, Object> data) {
        logger.info("Requisição de sincronização recebida");
        return ResponseEntity.ok(mcpService.syncData(data));
    }
} 