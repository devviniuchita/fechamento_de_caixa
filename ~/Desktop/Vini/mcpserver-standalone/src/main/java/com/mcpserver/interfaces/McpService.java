package com.mcpserver.interfaces;

import java.util.Map;

/**
 * Interface principal para serviços MCP (Manus Cursor Protocol)
 * Define operações básicas para integração com Cursor IDE
 */
public interface McpService {
    
    /**
     * Verifica o status do serviço MCP
     * @return Mapa contendo informações de status incluindo versão e estado
     */
    Map<String, Object> getStatus();
    
    /**
     * Executa um comando específico no servidor
     * @param command Comando a ser executado
     * @param parameters Parâmetros do comando
     * @return Resultado da execução do comando
     */
    Map<String, Object> executeCommand(String command, Map<String, Object> parameters);
    
    /**
     * Verifica a saúde do serviço
     * @return true se o serviço estiver saudável, false caso contrário
     */
    boolean healthCheck();
    
    /**
     * Sincroniza dados entre cliente e servidor
     * @param data Dados a serem sincronizados
     * @return Dados sincronizados
     */
    Map<String, Object> syncData(Map<String, Object> data);
} 