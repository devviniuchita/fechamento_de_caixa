package com.mcpserver.controller;

import com.mcpserver.model.McpRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mcp")
public class McpController {

    @Value("${mcp.api-key}")
    private String apiKey;

    @PostMapping
    public Object handleMcpRequest(@RequestBody McpRequest request, @RequestHeader("Authorization") String authorization) {
        // Validar a chave de API
        validateApiKey(authorization);
        
        switch (request.getMethod()) {
            case "getContext":
                return getContextData();
            case "executeCommand":
                return executeCommand(request.getParams());
            case "getFileList":
                return getFileList();
            case "getSystemInfo":
                return getSystemInfo();
            default:
                throw new IllegalArgumentException("Método não suportado: " + request.getMethod());
        }
    }

    private void validateApiKey(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ") || 
            !authorization.substring(7).equals(apiKey)) {
            throw new SecurityException("Chave de API inválida");
        }
    }

    private Map<String, Object> getContextData() {
        Map<String, Object> context = new HashMap<>();
        Map<String, Object> metadata = new HashMap<>();
        
        metadata.put("project", "Fechamento de Caixa");
        metadata.put("framework", "Spring Boot");
        metadata.put("language", "Java");
        
        List<String> documents = Arrays.asList(
            "src/main/java/com/controle/fechamentocaixa",
            "src/main/resources/application.properties",
            "pom.xml"
        );
        
        context.put("documents", documents);
        context.put("metadata", metadata);
        
        Map<String, Object> result = new HashMap<>();
        result.put("context", context);
        return result;
    }

    private Map<String, Object> executeCommand(Object params) {
        // Implementação simplificada - em produção, usar ProcessBuilder com validações
        String command = params.toString();
        
        Map<String, Object> result = new HashMap<>();
        result.put("result", "Comando executado");
        result.put("command", command);
        result.put("output", "Saída do comando: " + command);
        return result;
    }

    private Map<String, Object> getFileList() {
        File rootDir = new File(".");
        List<String> files = Arrays.stream(rootDir.listFiles())
            .filter(file -> !file.isHidden())
            .map(File::getName)
            .collect(Collectors.toList());
            
        Map<String, Object> result = new HashMap<>();
        result.put("files", files);
        return result;
    }

    private Map<String, Object> getSystemInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("os", System.getProperty("os.name"));
        info.put("java", System.getProperty("java.version"));
        info.put("user", System.getProperty("user.name"));
        info.put("workdir", System.getProperty("user.dir"));
        
        Map<String, Object> result = new HashMap<>();
        result.put("system", info);
        return result;
    }

    @GetMapping("/test")
    public String testCommunication() {
        return "Olá, Manus. Consegue se comunicar?";
    }

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("protocol", "MCP");
        status.put("framework", "Spring Boot");
        status.put("version", "1.0.0");
        return status;
    }
}
