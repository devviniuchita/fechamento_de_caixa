package com.mcpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.BindException;
import java.util.Collections;

/**
 * Classe principal de inicialização do MCP Server
 * Implementa mecanismo de fallback de portas para garantir
 * que o servidor inicie mesmo se a porta padrão estiver ocupada
 */
@SpringBootApplication
public class McpServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(McpServerApplication.class);
    
    // Porta padrão
    private static final int DEFAULT_PORT = 8080;
    // Número máximo de tentativas de portas alternativas
    private static final int MAX_PORT_ATTEMPTS = 10;
    // Intervalo de portas alternativas
    private static final int PORT_INCREMENT = 1;

    public static void main(String[] args) {
        // Primeira tentativa com a porta padrão ou a especificada via variável de ambiente
        int port = determinePort();
        boolean started = false;
        int attempts = 0;
        
        while (!started && attempts < MAX_PORT_ATTEMPTS) {
            try {
                logger.info("Tentando iniciar MCP Server na porta {}", port);
                ConfigurableApplicationContext context = startApplication(port);
                started = true;
                logger.info("MCP Server iniciado com sucesso na porta {}", port);
                logApplicationInfo(context, port);
            } catch (Exception e) {
                if (isPortBindingException(e)) {
                    logger.warn("Porta {} já está em uso. Tentando próxima porta.", port);
                    port += PORT_INCREMENT;
                    attempts++;
                } else {
                    logger.error("Erro ao iniciar MCP Server", e);
                    throw e;
                }
            }
        }
        
        if (!started) {
            String errorMsg = "Não foi possível iniciar o MCP Server após " + MAX_PORT_ATTEMPTS + " tentativas";
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }
    
    /**
     * Inicia a aplicação Spring Boot na porta especificada
     */
    private static ConfigurableApplicationContext startApplication(int port) {
        SpringApplication app = new SpringApplication(McpServerApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", String.valueOf(port)));
        return app.run();
    }
    
    /**
     * Verifica se a exceção está relacionada a porta já em uso
     */
    private static boolean isPortBindingException(Exception e) {
        return e.getCause() instanceof BindException || 
               (e.getMessage() != null && e.getMessage().contains("Address already in use"));
    }
    
    /**
     * Determina a porta a ser utilizada, priorizando variável de ambiente
     */
    private static int determinePort() {
        String envPort = System.getenv("MCP_PORT");
        if (envPort != null && !envPort.isEmpty()) {
            try {
                return Integer.parseInt(envPort);
            } catch (NumberFormatException e) {
                logger.warn("Valor inválido para MCP_PORT: {}. Usando porta padrão: {}", envPort, DEFAULT_PORT);
            }
        }
        return DEFAULT_PORT;
    }
    
    /**
     * Loga informações importantes da aplicação
     */
    private static void logApplicationInfo(ConfigurableApplicationContext context, int port) {
        String version = context.getEnvironment().getProperty("application.version", "1.0.0");
        String profile = context.getEnvironment().getActiveProfiles().length > 0 ? 
                         context.getEnvironment().getActiveProfiles()[0] : "default";
                        
        logger.info("--------------------------------------------------------------");
        logger.info("    MCP Server (v{}) iniciado na porta: {}", version, port);
        logger.info("    Perfil ativo: {}", profile);
        logger.info("--------------------------------------------------------------");
    }
} 