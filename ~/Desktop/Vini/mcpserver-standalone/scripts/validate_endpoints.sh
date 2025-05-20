#!/bin/bash

# ======================================================
# Script de Validação de Endpoints do MCP Server
# Criado como parte da Fase de Validação do MCP Server
# ======================================================

# Configurações
ORIGINAL_SERVER=${1:-"http://localhost:8080"}
NEW_SERVER=${2:-"http://localhost:8081"}
LOG_FILE="endpoint_validation_results.log"
REPORT_FILE="endpoint_validation_report.md"
TEMP_DIR="/tmp/mcp_validation"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Criação de diretório temporário
mkdir -p $TEMP_DIR

# Inicialização de arquivos de log e relatório
echo "# Relatório de Validação de Endpoints do MCP Server" > $REPORT_FILE
echo "**Data de execução:** $(date '+%Y-%m-%d %H:%M:%S')" >> $REPORT_FILE
echo "**Servidor Original:** $ORIGINAL_SERVER" >> $REPORT_FILE
echo "**Novo Servidor:** $NEW_SERVER" >> $REPORT_FILE
echo "" >> $REPORT_FILE
echo "## Resultados dos Testes" >> $REPORT_FILE
echo "" >> $REPORT_FILE

echo "$(date '+%Y-%m-%d %H:%M:%S') - Iniciando validação de endpoints" > $LOG_FILE
echo "Servidor Original: $ORIGINAL_SERVER" >> $LOG_FILE
echo "Novo Servidor: $NEW_SERVER" >> $LOG_FILE
echo "----------------------------------------" >> $LOG_FILE

# Função para testar um endpoint GET
test_get_endpoint() {
    local endpoint=$1
    local description=$2
    local expected_status=$3
    
    echo -e "${BLUE}Testando endpoint GET $endpoint - $description${NC}"
    echo "$(date '+%Y-%m-%d %H:%M:%S') - Testando GET $endpoint" >> $LOG_FILE
    
    # Testa servidor original
    original_response=$(curl -s -o $TEMP_DIR/original_response.json -w "%{http_code}" $ORIGINAL_SERVER$endpoint)
    original_status=$original_response
    
    # Testa novo servidor
    new_response=$(curl -s -o $TEMP_DIR/new_response.json -w "%{http_code}" $NEW_SERVER$endpoint)
    new_status=$new_response
    
    # Verifica status HTTP
    if [ "$original_status" = "$expected_status" ] && [ "$new_status" = "$expected_status" ]; then
        status_match="✅ Status HTTP corresponde"
        echo -e "${GREEN}$status_match${NC}"
    else
        status_match="❌ Status HTTP difere - Original: $original_status, Novo: $new_status, Esperado: $expected_status"
        echo -e "${RED}$status_match${NC}"
    fi
    
    # Verifica estrutura da resposta (se status for 200)
    if [ "$original_status" = "200" ] && [ "$new_status" = "200" ]; then
        # Compara estrutura JSON
        diff_output=$(diff -u $TEMP_DIR/original_response.json $TEMP_DIR/new_response.json)
        if [ $? -eq 0 ]; then
            response_match="✅ Estrutura da resposta corresponde"
            echo -e "${GREEN}$response_match${NC}"
        else
            response_match="❌ Estrutura da resposta difere"
            echo -e "${RED}$response_match${NC}"
            echo "Diferenças:" >> $LOG_FILE
            echo "$diff_output" >> $LOG_FILE
        fi
    else
        response_match="⚠️ Não foi possível comparar respostas devido a status HTTP diferentes"
        echo -e "${YELLOW}$response_match${NC}"
    fi
    
    # Registra resultados no relatório
    echo "### $description (GET $endpoint)" >> $REPORT_FILE
    echo "" >> $REPORT_FILE
    echo "- $status_match" >> $REPORT_FILE
    echo "- $response_match" >> $REPORT_FILE
    echo "" >> $REPORT_FILE
    
    echo "----------------------------------------" >> $LOG_FILE
}

# Função para testar um endpoint POST
test_post_endpoint() {
    local endpoint=$1
    local description=$2
    local payload=$3
    local expected_status=$4
    
    echo -e "${BLUE}Testando endpoint POST $endpoint - $description${NC}"
    echo "$(date '+%Y-%m-%d %H:%M:%S') - Testando POST $endpoint" >> $LOG_FILE
    
    # Cria arquivo temporário com o payload
    echo $payload > $TEMP_DIR/payload.json
    
    # Testa servidor original
    original_response=$(curl -s -X POST -H "Content-Type: application/json" -d @$TEMP_DIR/payload.json -o $TEMP_DIR/original_response.json -w "%{http_code}" $ORIGINAL_SERVER$endpoint)
    original_status=$original_response
    
    # Testa novo servidor
    new_response=$(curl -s -X POST -H "Content-Type: application/json" -d @$TEMP_DIR/payload.json -o $TEMP_DIR/new_response.json -w "%{http_code}" $NEW_SERVER$endpoint)
    new_status=$new_response
    
    # Verifica status HTTP
    if [ "$original_status" = "$expected_status" ] && [ "$new_status" = "$expected_status" ]; then
        status_match="✅ Status HTTP corresponde"
        echo -e "${GREEN}$status_match${NC}"
    else
        status_match="❌ Status HTTP difere - Original: $original_status, Novo: $new_status, Esperado: $expected_status"
        echo -e "${RED}$status_match${NC}"
    fi
    
    # Verifica estrutura da resposta (se status for 200)
    if [ "$original_status" = "200" ] && [ "$new_status" = "200" ]; then
        # Compara estrutura JSON
        diff_output=$(diff -u $TEMP_DIR/original_response.json $TEMP_DIR/new_response.json)
        if [ $? -eq 0 ]; then
            response_match="✅ Estrutura da resposta corresponde"
            echo -e "${GREEN}$response_match${NC}"
        else
            response_match="❌ Estrutura da resposta difere"
            echo -e "${RED}$response_match${NC}"
            echo "Diferenças:" >> $LOG_FILE
            echo "$diff_output" >> $LOG_FILE
        fi
    else
        response_match="⚠️ Não foi possível comparar respostas devido a status HTTP diferentes"
        echo -e "${YELLOW}$response_match${NC}"
    fi
    
    # Registra resultados no relatório
    echo "### $description (POST $endpoint)" >> $REPORT_FILE
    echo "" >> $REPORT_FILE
    echo "- $status_match" >> $REPORT_FILE
    echo "- $response_match" >> $REPORT_FILE
    echo "- Payload utilizado: \`$payload\`" >> $REPORT_FILE
    echo "" >> $REPORT_FILE
    
    echo "----------------------------------------" >> $LOG_FILE
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

# Finalização do relatório
echo "## Resumo" >> $REPORT_FILE
echo "" >> $REPORT_FILE
echo "Testes realizados em $(date '+%Y-%m-%d %H:%M:%S')" >> $REPORT_FILE
echo "" >> $REPORT_FILE
echo "Para detalhes completos, consulte o arquivo de log: $LOG_FILE" >> $REPORT_FILE

echo -e "${GREEN}Validação concluída!${NC}"
echo -e "${GREEN}Relatório gerado: $REPORT_FILE${NC}"
echo -e "${GREEN}Log detalhado: $LOG_FILE${NC}"

# Notificação para o sistema de comunicação da Equipe
if [ -f "../../.manus/scripts/communication.sh" ]; then
    ../../.manus/scripts/communication.sh enviar "manus" "equipe" "alerta" "Validação de endpoints concluída. Relatório disponível em $REPORT_FILE" "false"
fi

exit 0 