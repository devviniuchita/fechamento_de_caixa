#!/bin/bash

# auto-recovery.sh - Script de monitoramento e recuperação automática
# 
# Este script monitora o status dos agentes e inicia automaticamente
# a recuperação se detectar problemas de comunicação ou perda de memória.
#
# Uso: ./auto-recovery.sh [start|stop|status]

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
LOG_FILE="$MANUS_DIR/logs/auto-recovery.log"
PID_FILE="$MANUS_DIR/logs/auto-recovery.pid"
CHECK_INTERVAL=300 # 5 minutos em segundos

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

# Função para verificar se os arquivos essenciais existem
check_essential_files() {
  log "Verificando arquivos essenciais..."
  
  local missing_files=0
  
  for file in "$CONTEXT_DIR/canais_oficiais.md" "$CONTEXT_DIR/comunicacao_equipes.md" "$TASKS_FILE" "$STATUS_FILE" "$RECOVERY_SCRIPT"; do
    if [ ! -f "$file" ]; then
      log "ERRO: Arquivo essencial não encontrado: $file" "print"
      missing_files=$((missing_files + 1))
    fi
  done
  
  if [ $missing_files -eq 0 ]; then
    log "✅ Todos os arquivos essenciais estão presentes."
    return 0
  else
    log "❌ $missing_files arquivo(s) essencial(is) não encontrado(s)."
    return 1
  fi
}

# Função para verificar se o processo auto-recovery já está rodando
check_if_running() {
  if [ -f "$PID_FILE" ]; then
    local pid=$(cat "$PID_FILE")
    if ps -p "$pid" > /dev/null; then
      return 0
    fi
  fi
  return 1
}

# Função para iniciar o monitoramento
start_monitoring() {
  if check_if_running; then
    echo -e "${YELLOW}⚠️ O monitoramento já está em execução (PID: $(cat "$PID_FILE"))${NC}"
    return 1
  fi
  
  check_essential_files
  
  # Iniciar o processo em background
  nohup bash -c "exec $0 run" > "$MANUS_DIR/logs/auto-recovery.out" 2>&1 &
  echo $! > "$PID_FILE"
  
  echo -e "${GREEN}✅ Monitoramento iniciado em background (PID: $(cat "$PID_FILE"))${NC}"
  echo -e "${BLUE}Logs em: $LOG_FILE${NC}"
  
  # Realizar verificação inicial imediata
  log "Executando verificação inicial..."
  bash "$RECOVERY_SCRIPT" status
  
  return 0
}

# Função para parar o monitoramento
stop_monitoring() {
  if check_if_running; then
    local pid=$(cat "$PID_FILE")
    kill "$pid" 2>/dev/null
    rm -f "$PID_FILE"
    echo -e "${GREEN}✅ Monitoramento parado (PID: $pid)${NC}"
    log "Monitoramento parado pelo usuário."
    return 0
  else
    echo -e "${YELLOW}⚠️ O monitoramento não está em execução.${NC}"
    return 1
  fi
}

# Função para verificar o status do monitoramento
status_monitoring() {
  if check_if_running; then
    echo -e "${GREEN}✅ O monitoramento está ativo (PID: $(cat "$PID_FILE"))${NC}"
    echo -e "${BLUE}Logs em: $LOG_FILE${NC}"
    echo -e "${YELLOW}Últimas entradas do log:${NC}"
    tail -n 5 "$LOG_FILE" | sed 's/^/  /'
    return 0
  else
    echo -e "${RED}❌ O monitoramento não está ativo.${NC}"
    return 1
  fi
}

# Função para verificar se há problemas que exigem recuperação
check_for_recovery_needs() {
  log "Verificando necessidade de recuperação..."
  
  local need_recovery=false
  local reason=""
  
  # 1. Verificar status dos agentes no status_sync.json
  if [ -f "$STATUS_FILE" ]; then
    # Verificar se Manus está marcado como inativo ou com problemas
    if grep -q '"manus": *"INATIVO"' "$STATUS_FILE" || grep -q '"manus": *"PROBLEMA"' "$STATUS_FILE"; then
      need_recovery=true
      reason="Manus marcado como inativo ou com problemas no status_sync.json"
    fi
    
    # Verificar quando foi a última atualização
    local last_updated=$(grep -o '"last_updated": *"[^"]*"' "$STATUS_FILE" | cut -d'"' -f4)
    if [ -n "$last_updated" ]; then
      local last_updated_ts=$(date -d "$last_updated" +%s 2>/dev/null)
      local current_ts=$(date +%s)
      
      if [ -n "$last_updated_ts" ] && [ $((current_ts - last_updated_ts)) -gt 3600 ]; then # 1 hora
        need_recovery=true
        reason="Status não atualizado por mais de 1 hora"
      fi
    fi
  else
    need_recovery=true
    reason="Arquivo de status não encontrado"
  fi
  
  # 2. Verificar se o dialog.txt foi atualizado recentemente
  if [ -f "$DIALOG_FILE" ] && [ -s "$DIALOG_FILE" ]; then
    local last_modified=$(date -r "$DIALOG_FILE" +%s)
    local current_ts=$(date +%s)
    
    if [ $((current_ts - last_modified)) -gt 7200 ]; then # 2 horas
      need_recovery=true
      reason="$reason, dialog.txt não atualizado por mais de 2 horas"
    fi
  fi
  
  # 3. Verificar se o recovery_count aumentou muito (mais de 5 recuperações nas últimas 24h)
  if [ -f "$STATUS_FILE" ]; then
    local recovery_count=$(grep -o '"recovery_count": *[0-9]*' "$STATUS_FILE" | cut -d: -f2 | tr -d ' ')
    if [ -n "$recovery_count" ] && [ "$recovery_count" -gt 5 ]; then
      log "⚠️ Alerta: Muitas recuperações nas últimas 24h ($recovery_count)"
    fi
  fi
  
  if [ "$need_recovery" = true ]; then
    log "🔄 Necessária recuperação: $reason" "print"
    return 0
  else
    log "✅ Nenhuma necessidade de recuperação detectada."
    return 1
  fi
}

# Função para executar o monitoramento contínuo
run_monitoring() {
  log "Iniciando monitoramento contínuo..."
  
  # Aprender o intervalo de verificação do tasks.json se disponível
  if [ -f "$TASKS_FILE" ]; then
    local configured_interval=$(grep -o '"check_interval": *[0-9]*' "$TASKS_FILE" | cut -d: -f2 | tr -d ' ')
    if [ -n "$configured_interval" ] && [ "$configured_interval" -gt 0 ]; then
      CHECK_INTERVAL=$configured_interval
      log "Usando intervalo de verificação configurado: $CHECK_INTERVAL segundos"
    fi
  fi
  
  # Loop de monitoramento
  while true; do
    if check_for_recovery_needs; then
      log "Iniciando processo de recuperação automática..."
      bash "$RECOVERY_SCRIPT" reset
      log "Recuperação automática executada."
      
      # Registrar no dialog.txt
      echo "[SISTEMA] ALERTA: Auto-recovery executado devido a problemas detectados." >> "$DIALOG_FILE"
    else
      log "Sistema funcionando normalmente. Próxima verificação em $CHECK_INTERVAL segundos."
    fi
    
    # Aguardar o próximo ciclo
    sleep $CHECK_INTERVAL
  done
}

# Função principal
main() {
  local action=${1:-"status"}
  
  case "$action" in
    "start")
      start_monitoring
      ;;
    "stop")
      stop_monitoring
      ;;
    "status")
      status_monitoring
      ;;
    "run")
      # Usado internamente para o processo background
      run_monitoring
      ;;
    *)
      echo -e "${BLUE}=== AUTO-RECOVERY: MONITORAMENTO E RECUPERAÇÃO AUTOMÁTICA ===${NC}"
      echo -e "Uso: ./auto-recovery.sh [start|stop|status]"
      echo -e "  start  - Inicia o monitoramento em background"
      echo -e "  stop   - Para o monitoramento em execução"
      echo -e "  status - Mostra o status atual do monitoramento"
      ;;
  esac
}

# Executar função principal
main "$@" 