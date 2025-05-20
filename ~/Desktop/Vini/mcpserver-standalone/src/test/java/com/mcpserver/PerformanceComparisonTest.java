package com.mcpserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes de performance comparativos entre o projeto original e o novo MCP Server
 * Mede e compara tempos de resposta para garantir performance adequada
 */
@SpringBootTest
@EnabledIfSystemProperty(named = "performance.test", matches = "true")
public class PerformanceComparisonTest {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceComparisonTest.class);
    
    private RestTemplate restTemplate;
    private String originalServerUrl;
    private String newServerUrl;
    private HttpHeaders headers;
    
    // Configuração dos testes de performance
    private static final int WARMUP_REQUESTS = 10;
    private static final int TEST_REQUESTS = 50;
    private static final double ACCEPTABLE_PERFORMANCE_RATIO = 1.2; // Nova implementação pode ser até 20% mais lenta
    
    @BeforeEach
    public void setUp() {
        restTemplate = new RestTemplate();
        
        // URLs dos servidores para comparação
        originalServerUrl = System.getProperty("original.server.url", "http://localhost:8080");
        newServerUrl = System.getProperty("new.server.url", "http://localhost:8081");
        
        // Configura headers padrão
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Aquecimento dos servidores
        warmupServers();
    }
    
    /**
     * Realiza requisições de aquecimento para ambos os servidores
     * Isso minimiza o impacto do "cold start" nos testes de performance
     */
    private void warmupServers() {
        logger.info("Iniciando aquecimento dos servidores...");
        
        for (int i = 0; i < WARMUP_REQUESTS; i++) {
            try {
                restTemplate.getForEntity(originalServerUrl + "/api/mcp/status", String.class);
                restTemplate.getForEntity(newServerUrl + "/api/mcp/status", String.class);
            } catch (Exception e) {
                logger.warn("Erro durante aquecimento: {}", e.getMessage());
            }
        }
        
        try {
            // Pequena pausa para estabilização
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("Aquecimento dos servidores concluído");
    }
    
    /**
     * Compara a performance do endpoint /api/mcp/status
     */
    @Test
    public void compareStatusEndpointPerformance() {
        String endpoint = "/api/mcp/status";
        
        List<Long> originalTimes = new ArrayList<>();
        List<Long> newTimes = new ArrayList<>();
        
        // Executa as requisições e mede o tempo
        for (int i = 0; i < TEST_REQUESTS; i++) {
            // Testa servidor original
            long startTime = System.nanoTime();
            restTemplate.getForEntity(originalServerUrl + endpoint, String.class);
            long endTime = System.nanoTime();
            originalTimes.add(endTime - startTime);
            
            // Testa novo servidor
            startTime = System.nanoTime();
            restTemplate.getForEntity(newServerUrl + endpoint, String.class);
            endTime = System.nanoTime();
            newTimes.add(endTime - startTime);
        }
        
        // Calcula tempos médios
        double originalAvgMs = calculateAverageMs(originalTimes);
        double newAvgMs = calculateAverageMs(newTimes);
        
        logger.info("Performance do endpoint {}: Original: {:.2f} ms, Novo: {:.2f} ms", 
                endpoint, originalAvgMs, newAvgMs);
        
        // Verifica se a performance está dentro dos limites aceitáveis
        double ratio = newAvgMs / originalAvgMs;
        logger.info("Razão de performance (novo/original): {:.2f}", ratio);
        
        assertTrue(ratio <= ACCEPTABLE_PERFORMANCE_RATIO, 
                String.format("Nova implementação é %.2f vezes mais lenta que a original. " +
                        "Limite aceitável: %.2f", ratio, ACCEPTABLE_PERFORMANCE_RATIO));
    }
    
    /**
     * Compara a performance do endpoint /api/mcp/execute
     */
    @Test
    public void compareExecuteEndpointPerformance() {
        String endpoint = "/api/mcp/execute";
        
        List<Long> originalTimes = new ArrayList<>();
        List<Long> newTimes = new ArrayList<>();
        
        // Prepara o corpo da requisição
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("command", "ping");
        requestBody.put("requestId", "perf-test");
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        // Executa as requisições e mede o tempo
        for (int i = 0; i < TEST_REQUESTS; i++) {
            // Testa servidor original
            long startTime = System.nanoTime();
            restTemplate.postForEntity(originalServerUrl + endpoint, entity, String.class);
            long endTime = System.nanoTime();
            originalTimes.add(endTime - startTime);
            
            // Testa novo servidor
            startTime = System.nanoTime();
            restTemplate.postForEntity(newServerUrl + endpoint, entity, String.class);
            endTime = System.nanoTime();
            newTimes.add(endTime - startTime);
        }
        
        // Calcula tempos médios
        double originalAvgMs = calculateAverageMs(originalTimes);
        double newAvgMs = calculateAverageMs(newTimes);
        
        logger.info("Performance do endpoint {} (comando ping): Original: {:.2f} ms, Novo: {:.2f} ms", 
                endpoint, originalAvgMs, newAvgMs);
        
        // Verifica se a performance está dentro dos limites aceitáveis
        double ratio = newAvgMs / originalAvgMs;
        logger.info("Razão de performance (novo/original): {:.2f}", ratio);
        
        assertTrue(ratio <= ACCEPTABLE_PERFORMANCE_RATIO, 
                String.format("Nova implementação é %.2f vezes mais lenta que a original. " +
                        "Limite aceitável: %.2f", ratio, ACCEPTABLE_PERFORMANCE_RATIO));
    }
    
    /**
     * Compara a performance do endpoint /api/mcp/sync com carga moderada
     */
    @Test
    public void compareSyncEndpointPerformance() {
        String endpoint = "/api/mcp/sync";
        
        List<Long> originalTimes = new ArrayList<>();
        List<Long> newTimes = new ArrayList<>();
        
        // Prepara o corpo da requisição com dados moderados
        Map<String, Object> requestBody = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            requestBody.put("key" + i, "value" + i);
        }
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        // Executa as requisições e mede o tempo
        for (int i = 0; i < TEST_REQUESTS; i++) {
            // Testa servidor original
            long startTime = System.nanoTime();
            restTemplate.postForEntity(originalServerUrl + endpoint, entity, String.class);
            long endTime = System.nanoTime();
            originalTimes.add(endTime - startTime);
            
            // Testa novo servidor
            startTime = System.nanoTime();
            restTemplate.postForEntity(newServerUrl + endpoint, entity, String.class);
            endTime = System.nanoTime();
            newTimes.add(endTime - startTime);
        }
        
        // Calcula tempos médios
        double originalAvgMs = calculateAverageMs(originalTimes);
        double newAvgMs = calculateAverageMs(newTimes);
        
        logger.info("Performance do endpoint {}: Original: {:.2f} ms, Novo: {:.2f} ms", 
                endpoint, originalAvgMs, newAvgMs);
        
        // Verifica se a performance está dentro dos limites aceitáveis
        double ratio = newAvgMs / originalAvgMs;
        logger.info("Razão de performance (novo/original): {:.2f}", ratio);
        
        assertTrue(ratio <= ACCEPTABLE_PERFORMANCE_RATIO, 
                String.format("Nova implementação é %.2f vezes mais lenta que a original. " +
                        "Limite aceitável: %.2f", ratio, ACCEPTABLE_PERFORMANCE_RATIO));
    }
    
    /**
     * Calcula a média em milissegundos de uma lista de tempos em nanossegundos
     */
    private double calculateAverageMs(List<Long> nanoTimes) {
        // Remove os 10% de valores mais extremos para reduzir o impacto de outliers
        int removeCount = (int) (nanoTimes.size() * 0.1);
        
        // Ordena a lista
        nanoTimes.sort(Long::compare);
        
        // Remove os extremos
        List<Long> filteredTimes = nanoTimes.subList(removeCount, nanoTimes.size() - removeCount);
        
        // Calcula a média
        double sum = 0;
        for (Long time : filteredTimes) {
            sum += time;
        }
        
        // Converte para milissegundos
        return sum / filteredTimes.size() / 1_000_000.0;
    }
} 