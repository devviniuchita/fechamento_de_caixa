package com.mcpserver.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para API Básica do MCP Server
 * Criado para a Fase de Validação do plano de ação
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ApiBasicIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        this.baseUrl = "http://localhost:" + port;
    }

    @Test
    @DisplayName("API-01: Verificar status do servidor")
    public void testServerStatus() {
        // Execução
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/api/status", Map.class);
        
        // Verificação
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        
        // Registro de resultados
        logTestResult("API-01", "Verificar status do servidor", 
                HttpStatus.OK.equals(response.getStatusCode()) 
                        && "UP".equals(response.getBody().get("status")));
    }

    @Test
    @DisplayName("API-02: Autenticação bem-sucedida")
    public void testSuccessfulAuthentication() {
        // Preparação
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "test");
        credentials.put("password", "test123");
        
        // Execução
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/auth", credentials, Map.class);
        
        // Verificação
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().get("token"));
        
        // Registro de resultados
        logTestResult("API-02", "Autenticação bem-sucedida", 
                HttpStatus.OK.equals(response.getStatusCode()) 
                        && response.getBody().containsKey("token"));
    }

    @Test
    @DisplayName("API-03: Autenticação com credenciais inválidas")
    public void testFailedAuthentication() {
        // Preparação
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "test");
        credentials.put("password", "invalid");
        
        // Execução
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/auth", credentials, Map.class);
        
        // Verificação
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        
        // Registro de resultados
        logTestResult("API-03", "Autenticação com credenciais inválidas",
                HttpStatus.UNAUTHORIZED.equals(response.getStatusCode()));
    }

    @Test
    @DisplayName("API-04: Acesso a endpoint protegido sem token")
    public void testSecuredEndpointWithoutToken() {
        // Execução
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/api/secure/data", Map.class);
        
        // Verificação
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        
        // Registro de resultados
        logTestResult("API-04", "Acesso a endpoint protegido sem token",
                HttpStatus.UNAUTHORIZED.equals(response.getStatusCode()));
    }

    @Test
    @DisplayName("API-05: Acesso a endpoint protegido com token válido")
    public void testSecuredEndpointWithValidToken() {
        // Preparação
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "test");
        credentials.put("password", "test123");
        
        ResponseEntity<Map> authResponse = restTemplate.postForEntity(
                baseUrl + "/api/auth", credentials, Map.class);
        
        String token = (String) authResponse.getBody().get("token");
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        
        // Execução
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/api/secure/data", HttpMethod.GET, entity, Map.class);
        
        // Verificação
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Registro de resultados
        logTestResult("API-05", "Acesso a endpoint protegido com token válido",
                HttpStatus.OK.equals(response.getStatusCode()));
    }
    
    /**
     * Registra o resultado do teste no arquivo de resultados
     */
    private void logTestResult(String testId, String description, boolean success) {
        // Aqui poderia ser implementado um registro em arquivo ou banco de dados
        System.out.println("=== Resultado do Teste ===");
        System.out.println("ID: " + testId);
        System.out.println("Descrição: " + description);
        System.out.println("Resultado: " + (success ? "SUCESSO" : "FALHA"));
        System.out.println("=========================");
        
        // Notificar a equipe via SLI
        // Como este é um ambiente de teste, não chamamos diretamente o script SLI
    }
} 