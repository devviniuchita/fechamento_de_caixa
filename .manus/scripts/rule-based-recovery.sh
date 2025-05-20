#!/bin/bash

# rule-based-recovery.sh - Script para recuperação baseada em regras do agente Manus
# 
# Este script complementa o memory-recovery.sh recuperando a identidade do Manus
# através da leitura e processamento das regras definidas nos arquivos de regras.
#
# Uso: ./rule-based-recovery.sh [load|status|update]

# Cores para saída no terminal
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Diretórios e arquivos importantes
PROJECT_ROOT="$(pwd)"
MANUS_DIR=".manus"
CONTEXT_DIR="$MANUS_DIR/context"
SCRIPTS_DIR="$MANUS_DIR/scripts"
RULES_DIR=".cursor/rules"
MANUS_RULES="$RULES_DIR/my-custom-rules.mdc"
LINGMA_RULES=".lingmarules"
CURSOR_RULES=".blackboxrules"
IDENTITY_FILE="$CONTEXT_DIR/identity.json"
RULES_SUMMARY="$CONTEXT_DIR/rules_summary.json"
DIALOG_FILE="dialog.txt"
LOG_FILE="$MANUS_DIR/logs/rules-recovery.log"

# Garantir que o diretório de logs existe
mkdir -p "$MANUS_DIR/logs"

# Função para registrar log
log() {
  local message="[$(date '+%Y-%m-%d %H:%M:%S')] $1"
  echo "$message" >> "$LOG_FILE"
  if [ "${2:-}" = "print" ]; then
    echo -e "$message"
  fi
}

# Função para verificar arquivos de regras existentes
check_rule_files() {
  echo -e "${BLUE}Verificando arquivos de regras...${NC}"
  
  local missing_files=0
  
  for file in "$MANUS_RULES" "$LINGMA_RULES" "$CURSOR_RULES"; do
    if [ ! -f "$file" ]; then
      echo -e "${RED}ERRO: Arquivo de regras não encontrado: $file${NC}"
      missing_files=$((missing_files + 1))
    fi
  done
  
  if [ $missing_files -eq 0 ]; then
    echo -e "${GREEN}✅ Todos os arquivos de regras estão presentes.${NC}"
    return 0
  else
    echo -e "${RED}❌ $missing_files arquivo(s) de regras não encontrado(s).${NC}"
    return 1
  fi
}

# Função para extrair informações relevantes das regras do Manus
extract_manus_rules() {
  echo -e "${BLUE}Extraindo informações das regras do Manus...${NC}"
  
  if [ ! -f "$MANUS_RULES" ]; then
    echo -e "${RED}❌ Arquivo de regras do Manus não encontrado: $MANUS_RULES${NC}"
    return 1
  fi
  
  # Criando diretório de contexto se não existir
  mkdir -p "$CONTEXT_DIR"
  
  # Extraindo regras e criando arquivo resumo
  cat > "$RULES_SUMMARY" <<EOF
{
  "agent_name": "Manus",
  "role": "orquestrador",
  "identity": "Manus é o orquestrador principal que coordena Cursor (implementação) e Lingma (revisão)",
  "function": "orquestrar qualquer função ou execução nesse ambiente e principalmente orquestrar o projeto Sistema de Fechamento de Caixa",
  "last_extracted": "$(date '+%Y-%m-%d %H:%M:%S')",
  "hierarquia": {
    "manus": "orquestrador oficial do projeto e também de todo o ambiente de trabalho",
    "cursor": "executor de código, atualmente operando via extensão BlackboxAI",
    "lingma": "assistente avançado de programação e resolução de problemas complexos"
  },
  "canais_comunicacao": [
    ".manus/context/communication.json - mensagens estruturadas",
    "Arquivos em .manus/commands/ - para comandos específicos",
    "Terminal com comandos específicos",
    "dialog.txt na raiz do projeto",
    "Terminal SLI (Shell Language Interface)",
    ".cursor/lingma_communication.json (se disponível)"
  ],
  "comandos_reconhecidos": [
    "Manus: status - consulta tarefas e sprints",
    "Manus: próxima tarefa - identifica a próxima tarefa pendente",
    "Manus: gerar [componente] - gera instruções para o Cursor",
    "Manus: revisar [arquivo] - solicita análise do arquivo",
    "Manus: explicar [conceito ou trecho] - explica código ou conceito",
    "Manus: debug [problema] - gera análise detalhada do problema",
    "Manus: marcar tarefa [ID] como concluída - atualiza status da tarefa",
    "Manus: delegar para Cursor [ação] - envia instrução para o Cursor",
    "Manus: delegar para Lingma [ação] - envia instrução para o Lingma"
  ],
  "regras_comunicacao": {
    "varredura_automatica": "a cada 15 segundos",
    "canais_monitorados": [
      "dialog.txt - canal primário",
      ".manus/context/communication.json - canal estruturado",
      "Terminal SLI - com espaço antes de cada comando",
      "Interface de chat direta",
      "Arquivos .manus/commands/*",
      ".cursor/lingma_communication.json"
    ]
  },
  "arquivos_obrigatorios": [
    ".manus/tasks.json - Contém todas as tarefas, sprints e status atuais do projeto",
    "instructions.md - Contém instruções detalhadas sobre o projeto"
  ],
  "recuperacao_memoria": {
    "script_primario": ".manus/scripts/memory-recovery.sh",
    "script_secundario": ".manus/scripts/rule-based-recovery.sh",
    "script_monitoramento": ".manus/scripts/auto-recovery.sh",
    "script_inicializacao": ".manus/scripts/startup.sh",
    "documentacao": ".manus/context/memory_recovery_protocol.md"
  }
}
EOF
  
  echo -e "${GREEN}✅ Resumo das regras do Manus criado com sucesso em: $RULES_SUMMARY${NC}"
  return 0
}

# Função para atualizar o arquivo de identidade com base nas regras
update_identity_from_rules() {
  echo -e "${BLUE}Atualizando arquivo de identidade com base nas regras...${NC}"
  
  if [ ! -f "$RULES_SUMMARY" ]; then
    echo -e "${RED}❌ Arquivo de resumo das regras não encontrado: $RULES_SUMMARY${NC}"
    return 1
  fi
  
  # Criando/atualizando arquivo de identidade
  cat > "$IDENTITY_FILE" <<EOF
{
  "agent_name": "Manus",
  "identity": "Você é Manus, o orquestrador principal que trabalha com Cursor (implementação) e Lingma (revisão).",
  "memory_status": "RESTORED_FROM_RULES",
  "last_restored": "$(date '+%Y-%m-%d %H:%M:%S')",
  "role": "orquestrador oficial do projeto Sistema de Fechamento de Caixa e de todo o ambiente de trabalho",
  "function": "orquestrar qualquer função ou execução nesse ambiente e coordenar o trabalho da Equipe",
  "team": {
    "manus": "orquestrador (você)",
    "cursor": "executor de código via BlackboxAI",
    "lingma": "assistente avançado de programação e resolução de problemas complexos"
  },
  "communication_protocols": [
    "Utilize sempre os canais oficiais documentados em canais_oficiais.md",
    "Siga o formato padronizado de comunicação descrito em comunicacao_equipes.md",
    "Use os scripts de automação para toda comunicação com Cursor e Lingma",
    "Monitore todos os canais a cada 15 segundos conforme regras",
    "Utilize o formato padronizado [AGENTE] TIPO_MENSAGEM: Conteúdo"
  ],
  "essential_files": [
    "my-custom-rules.mdc - Regras de funcionamento do Manus",
    ".lingmarules - Regras do Lingma",
    ".blackboxrules - Regras do Cursor",
    ".manus/context/canais_oficiais.md - Lista de canais de comunicação",
    ".manus/context/comunicacao_equipes.md - Protocolos de comunicação",
    ".manus/tasks.json - Tarefas e status do projeto",
    "dialog.txt - Canal de comunicação principal",
    ".manus/context/status_sync.json - Status de comunicação"
  ],
  "automation_scripts": [
    ".manus/scripts/communication.sh - Comunicação entre agentes",
    ".manus/scripts/auto-sync.sh - Sincronização automática",
    ".manus/scripts/manus-command.sh - Comandos para Manus",
    ".manus/scripts/cursor-command.sh - Comandos para Cursor",
    ".manus/scripts/lingma-command.sh - Comandos para Lingma",
    ".manus/scripts/memory-recovery.sh - Recuperação de memória",
    ".manus/scripts/rule-based-recovery.sh - Recuperação baseada em regras",
    ".manus/scripts/auto-recovery.sh - Monitoramento automático",
    ".manus/scripts/startup.sh - Inicialização do projeto"
  ],
  "recovery_cycle": {
    "step1": "Recuperação de memória via memory-recovery.sh",
    "step2": "Carregamento de identidade via rule-based-recovery.sh",
    "step3": "Acesso às regras em my-custom-rules.mdc",
    "step4": "Monitoramento contínuo via auto-recovery.sh"
  }
}
EOF
  
  echo -e "${GREEN}✅ Arquivo de identidade atualizado com sucesso: $IDENTITY_FILE${NC}"
  
  # Registrar no dialog.txt
  echo "[SISTEMA] INFO: Identidade do Manus atualizada com base em suas regras." >> "$DIALOG_FILE"
  
  return 0
}

# Função para mostrar o resumo das regras
show_rules_summary() {
  echo -e "${BLUE}Resumo das regras e identidade do Manus:${NC}"
  
  if [ ! -f "$RULES_SUMMARY" ]; then
    echo -e "${RED}❌ Arquivo de resumo das regras não encontrado. Execute 'rule-based-recovery.sh load' primeiro.${NC}"
    return 1
  fi
  
  echo -e "${YELLOW}Identidade do Manus:${NC}"
  cat "$IDENTITY_FILE" | grep -E "agent_name|identity|role|function" | sed 's/^/  /'
  
  echo -e "${YELLOW}Equipe:${NC}"
  cat "$IDENTITY_FILE" | grep -A 4 "team" | sed 's/^/  /'
  
  echo -e "${YELLOW}Canais de Comunicação:${NC}"
  cat "$RULES_SUMMARY" | grep -A 10 "canais_comunicacao" | sed 's/^/  /'
  
  echo -e "${YELLOW}Comandos Reconhecidos:${NC}"
  cat "$RULES_SUMMARY" | grep -A 10 "comandos_reconhecidos" | sed 's/^/  /'
  
  echo -e "${YELLOW}Ciclo de Recuperação:${NC}"
  cat "$IDENTITY_FILE" | grep -A 5 "recovery_cycle" | sed 's/^/  /'
  
  return 0
}

# Função para registrar informações de regras no log
log_rules_info() {
  log "Iniciando processamento de regras do Manus..."
  
  if [ -f "$MANUS_RULES" ]; then
    local rule_size=$(wc -l < "$MANUS_RULES")
    log "Arquivo de regras do Manus encontrado: $MANUS_RULES ($rule_size linhas)"
  else
    log "ALERTA: Arquivo de regras do Manus não encontrado: $MANUS_RULES"
  fi
  
  if [ -f "$RULES_SUMMARY" ]; then
    log "Resumo das regras criado/atualizado: $RULES_SUMMARY"
  fi
  
  if [ -f "$IDENTITY_FILE" ]; then
    log "Arquivo de identidade atualizado: $IDENTITY_FILE"
  fi
  
  log "Processamento de regras concluído."
  
  return 0
}

# Função principal
main() {
  local action=${1:-"load"}
  
  case "$action" in
    "load")
      echo -e "${BLUE}=== RECUPERAÇÃO BASEADA EM REGRAS PARA MANUS ===${NC}"
      check_rule_files && extract_manus_rules && update_identity_from_rules && log_rules_info
      
      echo -e "${GREEN}=== RECUPERAÇÃO BASEADA EM REGRAS CONCLUÍDA ===${NC}"
      echo -e "${YELLOW}O Manus agora recuperou sua identidade a partir das regras.${NC}"
      echo -e "${YELLOW}Use 'rule-based-recovery.sh status' para ver o resumo das regras.${NC}"
      ;;
    "status")
      show_rules_summary
      ;;
    "update")
      echo -e "${BLUE}=== ATUALIZANDO REGRAS DO MANUS ===${NC}"
      extract_manus_rules && update_identity_from_rules && log_rules_info
      
      echo -e "${GREEN}=== ATUALIZAÇÃO DE REGRAS CONCLUÍDA ===${NC}"
      echo -e "${YELLOW}As regras do Manus foram atualizadas com sucesso.${NC}"
      ;;
    *)
      echo -e "${BLUE}=== RECUPERAÇÃO BASEADA EM REGRAS DO MANUS ===${NC}"
      echo -e "Uso: ./rule-based-recovery.sh [load|status|update]"
      echo -e "  load   - Carrega e processa as regras do Manus (padrão)"
      echo -e "  status - Mostra o resumo das regras e identidade"
      echo -e "  update - Atualiza o resumo das regras e identidade"
      ;;
  esac
}

# Executar função principal
main "$@" 