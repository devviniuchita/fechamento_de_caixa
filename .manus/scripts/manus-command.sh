#!/bin/bash

# Script de comandos para Manus
# Uso: ./manus-command.sh [comando] [parâmetros]

COMM_SCRIPT=".manus/scripts/communication.sh"

# Função para enviar comando para Cursor
send_to_cursor() {
  local command_type=$1
  local content=$2
  local requires_response=${3:-true}
  
  bash $COMM_SCRIPT enviar "manus" "cursor" "$command_type" "$content" "$requires_response"
}

# Função para enviar comando para a Equipe (todos os agentes)
send_to_team() {
  local command_type=$1
  local content=$2
  local requires_response=${3:-true}
  
  # Adicionar ao dialog.txt para garantir visibilidade
  echo "[MANUS] COMUNICAÇÃO TRIDIRECIONAL: Equipe: $content" >> dialog.txt
  
  # Enviar para cada agente individualmente
  bash $COMM_SCRIPT enviar "manus" "cursor" "$command_type" "$content" "$requires_response"
  bash $COMM_SCRIPT enviar "manus" "lingma" "$command_type" "$content" "$requires_response"
  
  echo "Mensagem tridirecional enviada com sucesso para todos os agentes!"
}

# Função para ler respostas do Cursor
read_from_cursor() {
  bash $COMM_SCRIPT ler "manus"
}

# Função para iniciar uma tarefa
start_task() {
  local task_id=$1
  
  send_to_cursor "command" "Iniciar tarefa $task_id"
  bash $COMM_SCRIPT tarefa "$task_id"
}

# Função para iniciar uma sprint
start_sprint() {
  local sprint_id=$1
  
  send_to_cursor "command" "Iniciar sprint $sprint_id"
  bash $COMM_SCRIPT sprint "$sprint_id"
}

# Função para solicitar status
request_status() {
  send_to_cursor "query" "status"
  bash $COMM_SCRIPT status
}

# Função para mostrar ajuda
show_help() {
  echo "=== Comandos Manus ==="
  echo "Uso: ./manus-command.sh [comando] [parâmetros]"
  echo ""
  echo "Comandos:"
  echo "  enviar [tipo] [conteúdo] - Envia comando para Cursor"
  echo "  equipe [tipo] [conteúdo] - Envia comando para toda a Equipe (comunicação tridirecional)"
  echo "  ler - Lê respostas do Cursor"
  echo "  tarefa [id] - Inicia uma tarefa"
  echo "  sprint [id] - Inicia uma sprint"
  echo "  status - Solicita status atual"
  echo "  ajuda - Mostra esta mensagem de ajuda"
}

# Processamento principal
case "$1" in
  "enviar")
    send_to_cursor "$2" "$3" "$4"
    ;;
  "equipe")
    send_to_team "$2" "$3" "$4"
    ;;
  "ler")
    read_from_cursor
    ;;
  "tarefa")
    start_task "$2"
    ;;
  "sprint")
    start_sprint "$2"
    ;;
  "status")
    request_status
    ;;
  "ajuda"|*)
    show_help
    ;;
esac 