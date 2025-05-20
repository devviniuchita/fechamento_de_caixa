#!/bin/bash

# startup.sh - Script de inicialização do projeto
# 
# Este script deve ser executado toda vez que o projeto for aberto
# para garantir que todos os sistemas estejam funcionando e que
# Manus tenha todos os dados necessários para operar.
#
# Uso: ./startup.sh

# Cores para saída no terminal
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Diretórios e arquivos importantes
MANUS_DIR=".manus"
CONTEXT_DIR="$MANUS_DIR/context"
SCRIPTS_DIR="$MANUS_DIR/scripts"
TASKS_FILE="$MANUS_DIR/tasks.json"
STATUS_FILE="$CONTEXT_DIR/status_sync.json"
DIALOG_FILE="dialog.txt"
RECOVERY_SCRIPT="$SCRIPTS_DIR/memory-recovery.sh"
AUTO_RECOVERY_SCRIPT="$SCRIPTS_DIR/auto-recovery.sh"
LOG_FILE="$MANUS_DIR/logs/startup.log"

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

# Função para verificar permissões de execução dos scripts
check_script_permissions() {
  log "Verificando permissões dos scripts..."
  
  local fixed_scripts=0
  
  for script in "$SCRIPTS_DIR"/*.sh; do
    if [ -f "$script" ] && [ ! -x "$script" ]; then
      chmod +x "$script"
      log "Corrigidas permissões para: $script"
      fixed_scripts=$((fixed_scripts + 1))
    fi
  done
  
  if [ $fixed_scripts -gt 0 ]; then
    log "✅ Corrigidas permissões para $fixed_scripts scripts." "print"
  else
    log "✅ Todos os scripts já possuem permissão de execução."
  fi
}

# Função para executar a recuperação inicial
run_initial_recovery() {
  log "Executando recuperação inicial de memória..." "print"
  
  if [ -f "$RECOVERY_SCRIPT" ]; then
    bash "$RECOVERY_SCRIPT" reset
    local result=$?
    
    if [ $result -eq 0 ]; then
      log "✅ Recuperação inicial de memória concluída com sucesso." "print"
    else
      log "❌ Falha na recuperação inicial de memória (código $result)." "print"
    fi
  else
    log "❌ Script de recuperação não encontrado: $RECOVERY_SCRIPT" "print"
  fi
}

# Função para iniciar o monitoramento automático
start_auto_recovery() {
  log "Iniciando sistema de monitoramento automático..." "print"
  
  if [ -f "$AUTO_RECOVERY_SCRIPT" ]; then
    bash "$AUTO_RECOVERY_SCRIPT" start
    local result=$?
    
    if [ $result -eq 0 ]; then
      log "✅ Sistema de monitoramento automático iniciado com sucesso." "print"
    else
      log "⚠️ O sistema de monitoramento já estava ativo ou encontrou um problema (código $result)." "print"
    fi
  else
    log "❌ Script de monitoramento automático não encontrado: $AUTO_RECOVERY_SCRIPT" "print"
  fi
}

# Função para registrar a inicialização no dialog.txt
register_startup() {
  log "Registrando inicialização no dialog.txt..."
  
  echo "[SISTEMA] INFO: Projeto inicializado. Scripts de recuperação e monitoramento ativados." >> "$DIALOG_FILE"
}

# Função para exibir resumo do projeto
show_project_summary() {
  echo -e "${BLUE}=== SISTEMA DE FECHAMENTO DE CAIXA ===${NC}"
  echo -e "${GREEN}✅ Inicialização concluída${NC}"
  echo -e "${YELLOW}Para recuperar memória manualmente: ${NC}${MANUS_DIR}/scripts/memory-recovery.sh reset"
  echo -e "${YELLOW}Para verificar status do monitoramento: ${NC}${MANUS_DIR}/scripts/auto-recovery.sh status"
  echo -e "${YELLOW}Para ver tarefas atuais: ${NC}cat ${TASKS_FILE} | jq '.current_task'"
  echo ""
  echo -e "${BLUE}Projeto pronto para uso.${NC}"
}

# Função principal
main() {
  echo -e "${BLUE}=== INICIALIZANDO PROJETO ===${NC}"
  
  # 1. Verificar permissões dos scripts
  check_script_permissions
  
  # 2. Executar recuperação inicial
  run_initial_recovery
  
  # 3. Iniciar monitoramento automático
  start_auto_recovery
  
  # 4. Registrar inicialização
  register_startup
  
  # 5. Exibir resumo do projeto
  show_project_summary
}

# Executar função principal
main 