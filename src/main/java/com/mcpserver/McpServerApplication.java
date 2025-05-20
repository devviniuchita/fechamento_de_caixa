package com.mcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class McpServerApplication {
    public static void main(String[] args) {
        String fallbackPorts = System.getenv().getOrDefault("MCP_FALLBACK_PORTS", "8081,8082,9090,9091");
        String primaryPort = System.getenv().getOrDefault("MCP_PORT", "8080");

        for (String port : (primaryPort + "," + fallbackPorts).split(",")) {
            try {
                System.setProperty("server.port", port);
                SpringApplication.run(McpServerApplication.class, args);
                System.out.println("✅ MCP Server iniciado com sucesso na porta " + port);
                break;
            } catch (Exception e) {
                System.out.println("⚠️ Porta " + port + " falhou. Tentando próxima...");
            }
        }
    }
} 