package com.mcpserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcpserver.dto.CommandRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para o McpController
 * Valida o comportamento dos endpoints conforme definido na estratégia de validação
 */
@SpringBootTest
@AutoConfigureMockMvc
public class McpControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Testa o endpoint de status
     * Verifica se retorna os campos esperados e status HTTP 200
     */
    @Test
    public void testStatusEndpoint() throws Exception {
        mockMvc.perform(get("/api/mcp/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.version", notNullValue()))
                .andExpect(jsonPath("$.status", is("running")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    /**
     * Testa o endpoint de verificação de saúde
     * Verifica se retorna status "UP" e status HTTP 200
     */
    @Test
    public void testHealthCheckEndpoint() throws Exception {
        mockMvc.perform(get("/api/mcp/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    /**
     * Testa o endpoint de execução de comando
     * Verifica se o comando "ping" retorna a resposta esperada "pong"
     */
    @Test
    public void testExecuteCommandPing() throws Exception {
        CommandRequestDTO requestDTO = new CommandRequestDTO();
        requestDTO.setCommand("ping");
        requestDTO.setRequestId("test-123");

        mockMvc.perform(post("/api/mcp/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.results.response", is("pong")))
                .andExpect(jsonPath("$.requestId", is("test-123")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.executionTimeMs", notNullValue()));
    }

    /**
     * Testa o endpoint de execução de comando
     * Verifica se o comando "echo" retorna a mensagem enviada
     */
    @Test
    public void testExecuteCommandEcho() throws Exception {
        CommandRequestDTO requestDTO = new CommandRequestDTO();
        requestDTO.setCommand("echo");
        Map<String, Object> params = new HashMap<>();
        params.put("message", "hello world");
        requestDTO.setParameters(params);
        requestDTO.setRequestId("test-456");

        mockMvc.perform(post("/api/mcp/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.results.response", is("hello world")))
                .andExpect(jsonPath("$.requestId", is("test-456")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    /**
     * Testa o endpoint de execução de comando
     * Verifica se um comando inválido retorna erro apropriado
     */
    @Test
    public void testExecuteCommandInvalid() throws Exception {
        CommandRequestDTO requestDTO = new CommandRequestDTO();
        requestDTO.setCommand("invalid-command");
        requestDTO.setRequestId("test-789");

        mockMvc.perform(post("/api/mcp/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorMessage", containsString("Comando desconhecido")))
                .andExpect(jsonPath("$.requestId", is("test-789")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    /**
     * Testa o endpoint de sincronização
     * Verifica se retorna os dados processados corretamente
     */
    @Test
    public void testSyncDataEndpoint() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");

        mockMvc.perform(post("/api/mcp/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.receivedItems", is(2)))
                .andExpect(jsonPath("$.processedItems", is(2)))
                .andExpect(jsonPath("$.syncTimestamp", notNullValue()));
    }
} 