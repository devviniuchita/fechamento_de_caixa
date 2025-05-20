package com.mcpserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de compatibilidade entre o projeto original e o novo MCP Server
 * Estes testes comparam respostas para garantir comportamento consistente
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfSystemProperty(named = "compatibility.test", matches = "true")
public class EndpointCompatibilityTest {

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private String originalServerUrl;
    private String newServerUrl;
    private HttpHeaders headers;
    
    @BeforeEach
    public void setUp() {
        // URLs dos servidores para comparação
        // Estes valores podem ser sobrescritos por propriedades do sistema
        originalServerUrl = System.getProperty("original.server.url", "http://localhost:8080");
        newServerUrl = System.getProperty("new.server.url", "http://localhost:8081");
        
        // Configura headers padrão
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }
    
    /**
     * Compara as respostas do endpoint /api/mcp/status
     * Verifica se ambos servidores retornam as mesmas chaves e tipos
     */
    @Test
    public void compareStatusEndpoint() throws Exception {
        ResponseEntity<String> originalResponse = restTemplate.getForEntity(
                originalServerUrl + "/api/mcp/status", String.class);
        ResponseEntity<String> newResponse = restTemplate.getForEntity(
                newServerUrl + "/api/mcp/status", String.class);
        
        // Verifica status HTTP
        assertEquals(originalResponse.getStatusCode(), newResponse.getStatusCode(), 
                "Status HTTP deve ser o mesmo em ambos servidores");
        
        // Compara estrutura JSON
        JsonNode originalJson = objectMapper.readTree(originalResponse.getBody());
        JsonNode newJson = objectMapper.readTree(newResponse.getBody());
        
        assertNotNull(originalJson.get("name"), "Campo 'name' deve existir na resposta do servidor original");
        assertNotNull(newJson.get("name"), "Campo 'name' deve existir na resposta do novo servidor");
        
        assertNotNull(originalJson.get("version"), "Campo 'version' deve existir na resposta do servidor original");
        assertNotNull(newJson.get("version"), "Campo 'version' deve existir na resposta do novo servidor");
        
        assertNotNull(originalJson.get("status"), "Campo 'status' deve existir na resposta do servidor original");
        assertNotNull(newJson.get("status"), "Campo 'status' deve existir na resposta do novo servidor");
        
        assertEquals(originalJson.get("status").asText(), newJson.get("status").asText(),
                "Campo 'status' deve ter o mesmo valor em ambos servidores");
    }
    
    /**
     * Compara as respostas do endpoint /api/mcp/health
     * Verifica se ambos servidores retornam o mesmo status
     */
    @Test
    public void compareHealthEndpoint() throws Exception {
        ResponseEntity<String> originalResponse = restTemplate.getForEntity(
                originalServerUrl + "/api/mcp/health", String.class);
        ResponseEntity<String> newResponse = restTemplate.getForEntity(
                newServerUrl + "/api/mcp/health", String.class);
        
        // Verifica status HTTP
        assertEquals(originalResponse.getStatusCode(), newResponse.getStatusCode(), 
                "Status HTTP deve ser o mesmo em ambos servidores");
        
        // Compara estrutura JSON
        JsonNode originalJson = objectMapper.readTree(originalResponse.getBody());
        JsonNode newJson = objectMapper.readTree(newResponse.getBody());
        
        assertNotNull(originalJson.get("status"), "Campo 'status' deve existir na resposta do servidor original");
        assertNotNull(newJson.get("status"), "Campo 'status' deve existir na resposta do novo servidor");
        
        assertEquals(originalJson.get("status").asText(), newJson.get("status").asText(),
                "Campo 'status' deve ter o mesmo valor em ambos servidores");
    }
    
    /**
     * Compara as respostas do endpoint /api/mcp/execute para o comando ping
     * Verifica se ambos servidores retornam a mesma resposta para o comando ping
     */
    @Test
    public void compareExecutePingCommand() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("command", "ping");
        requestBody.put("requestId", "test-ping");
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> originalResponse = restTemplate.postForEntity(
                originalServerUrl + "/api/mcp/execute", entity, String.class);
        ResponseEntity<String> newResponse = restTemplate.postForEntity(
                newServerUrl + "/api/mcp/execute", entity, String.class);
        
        // Verifica status HTTP
        assertEquals(originalResponse.getStatusCode(), newResponse.getStatusCode(), 
                "Status HTTP deve ser o mesmo em ambos servidores");
        
        // Compara estrutura JSON
        JsonNode originalJson = objectMapper.readTree(originalResponse.getBody());
        JsonNode newJson = objectMapper.readTree(newResponse.getBody());
        
        // Verifica campos essenciais
        assertEquals(originalJson.path("success").asBoolean(), newJson.path("success").asBoolean(),
                "Campo 'success' deve ter o mesmo valor em ambos servidores");
        
        // Obtém a resposta do comando ping
        JsonNode originalResponseValue = originalJson.path("results").path("response");
        JsonNode newResponseValue = newJson.path("results").path("response");
        
        assertNotNull(originalResponseValue, "Campo 'results.response' deve existir na resposta do servidor original");
        assertNotNull(newResponseValue, "Campo 'results.response' deve existir na resposta do novo servidor");
        
        assertEquals(originalResponseValue.asText(), newResponseValue.asText(),
                "Resposta ao comando 'ping' deve ser a mesma em ambos servidores");
    }
    
    /**
     * Compara as respostas do endpoint /api/mcp/sync
     * Verifica se ambos servidores processam os mesmos dados corretamente
     */
    @Test
    public void compareSyncEndpoint() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("key1", "value1");
        requestBody.put("key2", "value2");
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> originalResponse = restTemplate.postForEntity(
                originalServerUrl + "/api/mcp/sync", entity, String.class);
        ResponseEntity<String> newResponse = restTemplate.postForEntity(
                newServerUrl + "/api/mcp/sync", entity, String.class);
        
        // Verifica status HTTP
        assertEquals(originalResponse.getStatusCode(), newResponse.getStatusCode(), 
                "Status HTTP deve ser o mesmo em ambos servidores");
        
        // Compara estrutura JSON
        JsonNode originalJson = objectMapper.readTree(originalResponse.getBody());
        JsonNode newJson = objectMapper.readTree(newResponse.getBody());
        
        // Verifica campos essenciais
        assertEquals(originalJson.path("success").asBoolean(), newJson.path("success").asBoolean(),
                "Campo 'success' deve ter o mesmo valor em ambos servidores");
        
        // Compara contagem de itens processados
        if (originalJson.has("receivedItems") && newJson.has("receivedItems")) {
            assertEquals(originalJson.get("receivedItems").asInt(), newJson.get("receivedItems").asInt(),
                    "Número de itens recebidos deve ser o mesmo em ambos servidores");
        }
    }
} 