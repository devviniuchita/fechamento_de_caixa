package com.mcpserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o McpServiceImpl
 */
public class McpServiceImplTest {

    private McpServiceImpl mcpService;

    @BeforeEach
    public void setUp() {
        mcpService = new McpServiceImpl();
        ReflectionTestUtils.setField(mcpService, "appVersion", "1.0.0-TEST");
        ReflectionTestUtils.setField(mcpService, "appName", "MCP Server Test");
    }

    /**
     * Testa o método getStatus
     * Verifica se retorna os campos esperados com valores corretos
     */
    @Test
    public void testGetStatus() {
        Map<String, Object> status = mcpService.getStatus();
        
        assertNotNull(status);
        assertEquals("MCP Server Test", status.get("name"));
        assertEquals("1.0.0-TEST", status.get("version"));
        assertEquals("running", status.get("status"));
        assertNotNull(status.get("timestamp"));
    }

    /**
     * Testa o método executeCommand com o comando "ping"
     * Verifica se retorna a resposta esperada "pong"
     */
    @Test
    public void testExecuteCommandPing() {
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> result = mcpService.executeCommand("ping", parameters);
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("pong", result.get("response"));
        assertNotNull(result.get("timestamp"));
    }

    /**
     * Testa o método executeCommand com o comando "echo"
     * Verifica se retorna a mensagem enviada
     */
    @Test
    public void testExecuteCommandEcho() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", "test message");
        
        Map<String, Object> result = mcpService.executeCommand("echo", parameters);
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("test message", result.get("response"));
        assertNotNull(result.get("timestamp"));
    }

    /**
     * Testa o método executeCommand com o comando "version"
     * Verifica se retorna a versão configurada
     */
    @Test
    public void testExecuteCommandVersion() {
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> result = mcpService.executeCommand("version", parameters);
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("1.0.0-TEST", result.get("version"));
        assertNotNull(result.get("timestamp"));
    }

    /**
     * Testa o método executeCommand com um comando inválido
     * Verifica se retorna erro apropriado
     */
    @Test
    public void testExecuteCommandInvalid() {
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> result = mcpService.executeCommand("invalid-command", parameters);
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("Comando desconhecido"));
        assertNotNull(result.get("timestamp"));
    }

    /**
     * Testa o método healthCheck
     * Verifica se retorna true
     */
    @Test
    public void testHealthCheck() {
        boolean isHealthy = mcpService.healthCheck();
        assertTrue(isHealthy);
    }

    /**
     * Testa o método syncData com dados válidos
     * Verifica se retorna sucesso e contagem correta de itens
     */
    @Test
    public void testSyncDataValid() {
        Map<String, Object> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");
        
        Map<String, Object> result = mcpService.syncData(data);
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals(2, result.get("receivedItems"));
        assertEquals(2, result.get("processedItems"));
        assertNotNull(result.get("syncTimestamp"));
    }

    /**
     * Testa o método syncData com dados nulos
     * Verifica se retorna erro apropriado
     */
    @Test
    public void testSyncDataNull() {
        Map<String, Object> result = mcpService.syncData(null);
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("Dados nulos"));
        assertNotNull(result.get("syncTimestamp"));
    }
} 