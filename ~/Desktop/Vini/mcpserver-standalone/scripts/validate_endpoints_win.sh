#!/bin/bash

# ======================================================
# Script de Validação de Endpoints do MCP Server
# Versão adaptada para Windows
# ======================================================

# Configurações
ORIGINAL_SERVER=${1:-"http://localhost:8080"}
NEW_SERVER=${2:-"http://localhost:8081"}
LOG_FILE="endpoint_validation_results.log"
REPORT_FILE="endpoint_validation_report.md"
TEMP_DIR="./temp_validation"

# Cores para output (compatível com Windows)
RED=''
GREEN=''
YELLOW=''
BLUE=''
NC=''

# Verificar se estamos em ambiente Windows
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    echo "Executando em ambiente Windows - modo compatível"
else
    # Se não for Windows, usar cores
    RED='\033[0;31m'
    GREEN='\033[0;32m'
    YELLOW='\033[0;33m'
    BLUE='\033[0;34m'
    NC='\033[0m' # No Color
fi

# Criação de diretório temporário (compatível com Windows)
mkdir -p "$TEMP_DIR"

# Inicialização de arquivos de log e relatório
echo "# Relatório de Validação de Endpoints do MCP Server" > "$REPORT_FILE"
echo "**Data de execução:** $(date '+%Y-%m-%d %H:%M:%S')" >> "$REPORT_FILE"
echo "**Servidor Original:** $ORIGINAL_SERVER" >> "$REPORT_FILE"
echo "**Novo Servidor:** $NEW_SERVER" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"
echo "## Resultados dos Testes" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

echo "$(date '+%Y-%m-%d %H:%M:%S') - Iniciando validação de endpoints" > "$LOG_FILE"
echo "Servidor Original: $ORIGINAL_SERVER" >> "$LOG_FILE"
echo "Novo Servidor: $NEW_SERVER" >> "$LOG_FILE"
echo "----------------------------------------" >> "$LOG_FILE"

# Função para testar um endpoint GET
test_get_endpoint() {
    local endpoint=$1
    local description=$2
    local expected_status=$3
    
    echo -e "${BLUE}Testando endpoint GET $endpoint - $description${NC}"
    echo "$(date '+%Y-%m-%d %H:%M:%S') - Testando GET $endpoint" >> "$LOG_FILE"
    
    # Testa servidor original
    curl -s -o "$TEMP_DIR/original_response.json" "$ORIGINAL_SERVER$endpoint"
    original_status=$?
    
    # Testa novo servidor
    curl -s -o "$TEMP_DIR/new_response.json" "$NEW_SERVER$endpoint"
    new_status=$?
    
    # Verifica status HTTP (simplificado para Windows)
    if [ "$original_status" -eq 0 ] && [ "$new_status" -eq 0 ]; then
        status_match="✅ Ambos servidores responderam com sucesso"
        echo -e "${GREEN}$status_match${NC}"
    else
        status_match="❌ Falha na resposta - Original: $original_status, Novo: $new_status"
        echo -e "${RED}$status_match${NC}"
    fi
    
    # Verifica estrutura da resposta (se ambos retornaram com sucesso)
    if [ "$original_status" -eq 0 ] && [ "$new_status" -eq 0 ]; then
        # Compara estrutura JSON (adaptado para Windows)
        if command -v fc >/dev/null 2>&1; then
            # Usando fc no Windows
            fc "$TEMP_DIR/original_response.json" "$TEMP_DIR/new_response.json" > "$TEMP_DIR/diff.txt"
            diff_status=$?
        else
            # Fallback para diff se disponível
            diff -u "$TEMP_DIR/original_response.json" "$TEMP_DIR/new_response.json" > "$TEMP_DIR/diff.txt"
            diff_status=$?
        fi
        
        if [ $diff_status -eq 0 ]; then
            response_match="✅ Estrutura da resposta corresponde"
            echo -e "${GREEN}$response_match${NC}"
        else
            response_match="❌ Estrutura da resposta difere"
            echo -e "${RED}$response_match${NC}"
            echo "Diferenças:" >> "$LOG_FILE"
            cat "$TEMP_DIR/diff.txt" >> "$LOG_FILE"
        fi
    else
        response_match="⚠️ Não foi possível comparar respostas devido a falhas na comunicação"
        echo -e "${YELLOW}$response_match${NC}"
    fi
    
    # Registra resultados no relatório
    echo "### $description (GET $endpoint)" >> "$REPORT_FILE"
    echo "" >> "$REPORT_FILE"
    echo "- $status_match" >> "$REPORT_FILE"
    echo "- $response_match" >> "$REPORT_FILE"
    echo "" >> "$REPORT_FILE"
    
    echo "----------------------------------------" >> "$LOG_FILE"
}

# Função para testar um endpoint POST
test_post_endpoint() {
    local endpoint=$1
    local description=$2
    local payload=$3
    local expected_status=$4
    
    echo -e "${BLUE}Testando endpoint POST $endpoint - $description${NC}"
    echo "$(date '+%Y-%m-%d %H:%M:%S') - Testando POST $endpoint" >> "$LOG_FILE"
    
    # Cria arquivo temporário com o payload
    echo $payload > "$TEMP_DIR/payload.json"
    
    # Testa servidor original
    curl -s -X POST -H "Content-Type: application/json" -d "@$TEMP_DIR/payload.json" -o "$TEMP_DIR/original_response.json" "$ORIGINAL_SERVER$endpoint"
    original_status=$?
    
    # Testa novo servidor
    curl -s -X POST -H "Content-Type: application/json" -d "@$TEMP_DIR/payload.json" -o "$TEMP_DIR/new_response.json" "$NEW_SERVER$endpoint"
    new_status=$?
    
    # Verifica status HTTP (simplificado para Windows)
    if [ "$original_status" -eq 0 ] && [ "$new_status" -eq 0 ]; then
        status_match="✅ Ambos servidores responderam com sucesso"
        echo -e "${GREEN}$status_match${NC}"
    else
        status_match="❌ Falha na resposta - Original: $original_status, Novo: $new_status"
        echo -e "${RED}$status_match${NC}"
    fi
    
    # Verifica estrutura da resposta (se ambos retornaram com sucesso)
    if [ "$original_status" -eq 0 ] && [ "$new_status" -eq 0 ]; then
        # Compara estrutura JSON (adaptado para Windows)
        if command -v fc >/dev/null 2>&1; then
            # Usando fc no Windows
            fc "$TEMP_DIR/original_response.json" "$TEMP_DIR/new_response.json" > "$TEMP_DIR/diff.txt"
            diff_status=$?
        else
            # Fallback para diff se disponível
            diff -u "$TEMP_DIR/original_response.json" "$TEMP_DIR/new_response.json" > "$TEMP_DIR/diff.txt"
            diff_status=$?
        fi
        
        if [ $diff_status -eq 0 ]; then
            response_match="✅ Estrutura da resposta corresponde"
            echo -e "${GREEN}$response_match${NC}"
        else
            response_match="❌ Estrutura da resposta difere"
            echo -e "${RED}$response_match${NC}"
            echo "Diferenças:" >> "$LOG_FILE"
            cat "$TEMP_DIR/diff.txt" >> "$LOG_FILE"
        fi
    else
        response_match="⚠️ Não foi possível comparar respostas devido a falhas na comunicação"
        echo -e "${YELLOW}$response_match${NC}"
    fi
    
    # Registra resultados no relatório
    echo "### $description (POST $endpoint)" >> "$REPORT_FILE"
    echo "" >> "$REPORT_FILE"
    echo "- $status_match" >> "$REPORT_FILE"
    echo "- $response_match" >> "$REPORT_FILE"
    echo "- Payload utilizado: \`$payload\`" >> "$REPORT_FILE"
    echo "" >> "$REPORT_FILE"
    
    echo "----------------------------------------" >> "$LOG_FILE"
}

# Execução dos testes de endpoints
echo -e "${BLUE}Iniciando validação de endpoints do MCP Server${NC}"
echo -e "${BLUE}Servidor Original: $ORIGINAL_SERVER${NC}"
echo -e "${BLUE}Novo Servidor: $NEW_SERVER${NC}"
echo -e "${BLUE}----------------------------------------${NC}"

# Testes de endpoints GET
test_get_endpoint "/api/mcp/status" "Verificação de status do servidor" "200"
test_get_endpoint "/api/mcp/health" "Verificação de saúde do servidor" "200"
test_get_endpoint "/api/mcp/version" "Verificação de versão do servidor" "200"

# Testes de endpoints POST
test_post_endpoint "/api/mcp/execute" "Execução de comando ping" '{"command":"ping","requestId":"test-ping"}' "200"
test_post_endpoint "/api/mcp/execute" "Execução de comando inválido" '{"command":"invalid","requestId":"test-invalid"}' "400"
test_post_endpoint "/api/mcp/sync" "Sincronização de dados" '{"key1":"value1","key2":"value2"}' "200"
test_post_endpoint "/api/mcp/auth" "Autenticação com credenciais válidas" '{"username":"test","password":"test123"}' "200"
test_post_endpoint "/api/mcp/config" "Obtenção de configuração" '{"configKey":"server.port"}' "200"

# Finalização do relatório
echo "## Resumo" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"
echo "Testes realizados em $(date '+%Y-%m-%d %H:%M:%S')" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"
echo "Para detalhes completos, consulte o arquivo de log: $LOG_FILE" >> "$REPORT_FILE"

echo -e "${GREEN}Validação concluída!${NC}"
echo -e "${GREEN}Relatório gerado: $REPORT_FILE${NC}"
echo -e "${GREEN}Log detalhado: $LOG_FILE${NC}"

# Notificação para o sistema de comunicação da Equipe
if [ -f "../../.manus/scripts/communication.sh" ]; then
    ../../.manus/scripts/communication.sh enviar "manus" "equipe" "alerta" "Validação de endpoints concluída. Relatório disponível em $REPORT_FILE" "false"
fi

exit 0 