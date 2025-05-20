#!/bin/bash

# memory-recovery.sh - Script para recuperação instantânea de memória do agente Manus
# 
# Este script ajuda a restaurar o estado e a memória de Manus após uma troca de modelo,
# garantindo que a comunicação com Cursor e Lingma seja mantida intacta.
#
# Uso: ./memory-recovery.sh [reset|status|heal]

# Cores para saída no terminal
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Diretórios e arquivos importantes
MANUS_DIR=".manus"
CONTEXT_DIR="$MANUS_DIR/context"
TASKS_FILE="$MANUS_DIR/tasks.json"
STATUS_FILE="$MANUS_DIR/context/status_sync.json"
DIALOG_FILE="dialog.txt"
COMM_SCRIPT="$MANUS_DIR/scripts/communication.sh"

# Função para verificar se os arquivos essenciais existem
check_essential_files() {
  echo -e "${BLUE}Verificando arquivos essenciais...${NC}"
  
  local missing_files=0
  
  for file in "$CONTEXT_DIR/canais_oficiais.md" "$CONTEXT_DIR/comunicacao_equipes.md" "$TASKS_FILE" "$COMM_SCRIPT"; do
    if [ ! -f "$file" ]; then
      echo -e "${RED}ERRO: Arquivo essencial não encontrado: $file${NC}"
      missing_files=$((missing_files + 1))
    fi
  done
  
  if [ $missing_files -eq 0 ]; then
    echo -e "${GREEN}✅ Todos os arquivos essenciais estão presentes.${NC}"
    return 0
  else
    echo -e "${RED}❌ $missing_files arquivo(s) essencial(is) não encontrado(s).${NC}"
    return 1
  fi
}

# Função para restaurar contexto imediatamente
restore_context() {
  echo -e "${BLUE}Iniciando restauração de contexto para Manus...${NC}"
  
  # 1. Registrar no dialog.txt que uma recuperação está acontecendo
  echo "[SISTEMA] ALERTA: Detectada mudança de modelo. Iniciando recuperação de memória para Manus." >> "$DIALOG_FILE"
  
  # 2. Enviar mensagem para Cursor e Lingma via communication.sh
  bash "$COMM_SCRIPT" enviar "sistema" "cursor" "alerta" "Detectada mudança de modelo para Manus. Mantenha protocolo de comunicação ativo." "false"
  bash "$COMM_SCRIPT" enviar "sistema" "lingma" "alerta" "Detectada mudança de modelo para Manus. Mantenha protocolo de comunicação ativo." "false"
  
  # 3. Criar/atualizar arquivo de notificação para sinalizar ao modelo que ele é Manus
  cat > "$CONTEXT_DIR/identity.json" <<EOF
{
  "agent_name": "Manus",
  "identity": "Você é Manus, o agente principal de coordenação que trabalha com Cursor (implementação) e Lingma (revisão).",
  "memory_status": "RESTORED",
  "last_restored": "$(date '+%Y-%m-%d %H:%M:%S')",
  "communication_protocols": [
    "Utilize sempre os canais oficiais documentados em canais_oficiais.md",
    "Siga o formato padronizado de comunicação descrito em comunicacao_equipes.md",
    "Use os scripts de automação para toda comunicação com Cursor e Lingma"
  ],
  "essential_files": [
    ".manus/context/canais_oficiais.md",
    ".manus/context/comunicacao_equipes.md",
    ".manus/tasks.json",
    "dialog.txt",
    ".manus/context/status_sync.json"
  ],
  "automation_scripts": [
    ".manus/scripts/communication.sh",
    ".manus/scripts/auto-sync.sh",
    ".manus/scripts/manus-command.sh",
    ".manus/scripts/cursor-command.sh",
    ".manus/scripts/lingma-command.sh",
    ".manus/scripts/memory-recovery.sh"
  ]
}
EOF

  echo -e "${GREEN}✅ Contexto de identidade restaurado.${NC}"
  
  # 4. Fazer backup e atualizar o status_sync.json
  if [ -f "$STATUS_FILE" ]; then
    cp "$STATUS_FILE" "$STATUS_FILE.bak"
    
    # Atualizar status
    cat > "$STATUS_FILE" <<EOF
{
  "last_updated": "$(date '+%Y-%m-%d %H:%M:%S')",
  "project_status": "ATIVO",
  "communication_status": {
    "manus": "RECUPERADO",
    "cursor": "ATIVO",
    "lingma": "ATIVO",
    "dialog_txt": "ATIVO",
    "terminal_sli": "ATIVO",
    "communication_json": "ATIVO"
  },
  "agents_heartbeat": {
    "manus": "$(date '+%Y-%m-%d %H:%M:%S')",
    "cursor": "ATIVO",
    "lingma": "ATIVO"
  },
  "recovery_info": {
    "last_recovery": "$(date '+%Y-%m-%d %H:%M:%S')",
    "recovery_count": 1,
    "recovered_by": "memory-recovery.sh"
  }
}
EOF
    echo -e "${GREEN}✅ Status atualizado com sucesso.${NC}"
  else
    echo -e "${YELLOW}⚠️ Arquivo status_sync.json não encontrado. Criando novo...${NC}"
    mkdir -p "$(dirname "$STATUS_FILE")"
    
    # Criar arquivo de status
    cat > "$STATUS_FILE" <<EOF
{
  "last_updated": "$(date '+%Y-%m-%d %H:%M:%S')",
  "project_status": "ATIVO",
  "communication_status": {
    "manus": "RECUPERADO",
    "cursor": "ATIVO",
    "lingma": "ATIVO",
    "dialog_txt": "ATIVO",
    "terminal_sli": "ATIVO",
    "communication_json": "ATIVO"
  },
  "agents_heartbeat": {
    "manus": "$(date '+%Y-%m-%d %H:%M:%S')",
    "cursor": "PENDENTE",
    "lingma": "PENDENTE"
  },
  "recovery_info": {
    "last_recovery": "$(date '+%Y-%m-%d %H:%M:%S')",
    "recovery_count": 1,
    "recovered_by": "memory-recovery.sh"
  }
}
EOF
    echo -e "${GREEN}✅ Novo arquivo de status criado.${NC}"
  fi
  
  # 5. Registrar no dialog.txt que a recuperação foi concluída
  echo "[SISTEMA] INFO: Recuperação de memória para Manus concluída com sucesso." >> "$DIALOG_FILE"
  echo -e "${GREEN}✅ Recuperação de memória concluída com sucesso.${NC}"
  
  return 0
}

# Função para mostrar o status atual
show_status() {
  echo -e "${BLUE}Status do agente Manus:${NC}"
  
  if [ -f "$STATUS_FILE" ]; then
    echo "Último status registrado em $(date -r "$STATUS_FILE" '+%Y-%m-%d %H:%M:%S')"
    echo -e "${YELLOW}Estado atual:${NC}"
    cat "$STATUS_FILE" | grep -E "project_status|communication_status|agents_heartbeat" | sed 's/^/  /'
    
    if grep -q "recovery_info" "$STATUS_FILE"; then
      echo -e "${YELLOW}Informações de recuperação:${NC}"
      cat "$STATUS_FILE" | grep -A 5 "recovery_info" | sed 's/^/  /'
    fi
  else
    echo -e "${RED}❌ Arquivo de status não encontrado. Execute './memory-recovery.sh reset' para inicializar.${NC}"
  fi
}

# Função para testar a comunicação com todos os agentes
test_communication() {
  echo -e "${BLUE}Testando comunicação com todos os agentes...${NC}"
  
  # 1. Verificar se o script de comunicação existe
  if [ ! -f "$COMM_SCRIPT" ]; then
    echo -e "${RED}❌ Script de comunicação não encontrado: $COMM_SCRIPT${NC}"
    return 1
  fi
  
  # 2. Enviar mensagem de teste para Cursor
  echo -e "${YELLOW}Enviando mensagem de teste para Cursor...${NC}"
  bash "$COMM_SCRIPT" enviar "manus" "cursor" "teste" "Teste de comunicação automática após recuperação de memória." "true"
  
  # 3. Enviar mensagem de teste para Lingma
  echo -e "${YELLOW}Enviando mensagem de teste para Lingma...${NC}"
  bash "$COMM_SCRIPT" enviar "manus" "lingma" "teste" "Teste de comunicação automática após recuperação de memória." "true"
  
  # 4. Registrar teste no dialog.txt
  echo "[MANUS] TESTE: Verificação de comunicação após recuperação de memória." >> "$DIALOG_FILE"
  
  echo -e "${GREEN}✅ Testes de comunicação enviados com sucesso.${NC}"
  return 0
}

# Função principal
main() {
  local action=${1:-"reset"}
  
  case "$action" in
    "reset")
      echo -e "${BLUE}=== RECUPERAÇÃO DE MEMÓRIA DO AGENTE MANUS ===${NC}"
      check_essential_files && restore_context && test_communication
      
      echo -e "${GREEN}=== RECUPERAÇÃO CONCLUÍDA ===${NC}"
      echo -e "${YELLOW}Recarregue o chat ou abra novamente o arquivo do projeto para que Manus recupere sua memória.${NC}"
      ;;
    "status")
      show_status
      ;;
    "heal")
      # Esta opção executa apenas o teste de comunicação
      test_communication
      ;;
    *)
      echo -e "${BLUE}=== SCRIPT DE RECUPERAÇÃO DE MEMÓRIA DO MANUS ===${NC}"
      echo -e "Uso: ./memory-recovery.sh [reset|status|heal]"
      echo -e "  reset  - Restaura completamente o contexto e memória (padrão)"
      echo -e "  status - Mostra o status atual do agente"
      echo -e "  heal   - Executa apenas o teste de comunicação"
      ;;
  esac
}

# Executar função principal
main "$@" 