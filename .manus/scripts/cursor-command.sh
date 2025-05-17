#!/bin/bash

# Script de comandos para Cursor
# Uso: ./cursor-command.sh [comando] [parâmetros]

COMM_SCRIPT=".manus/scripts/communication.sh"
TASKS_FILE=".manus/tasks.json"

# Função para enviar resposta para Manus
send_to_manus() {
  local response_type=$1
  local content=$2
  local requires_response=${3:-false}
  
  bash $COMM_SCRIPT enviar "cursor" "manus" "$response_type" "$content" "$requires_response"
}

# Função para ler comandos de Manus
read_from_manus() {
  bash $COMM_SCRIPT ler "cursor"
}

# Função para executar uma tarefa
execute_task() {
  local task_id=$1
  local task_info=$(jq --arg id "$task_id" '.sprints[].tarefas[] | select(.id == $id)' $TASKS_FILE)
  
  if [ -z "$task_info" ]; then
    send_to_manus "error" "Tarefa $task_id não encontrada"
    return 1
  fi
  
  local task_desc=$(echo "$task_info" | jq -r '.descricao')
  
  # Atualizar estado da tarefa para 'em andamento'
  jq --arg id "$task_id" \
     '(.sprints[] | .tarefas[] | select(.id == $id)).status = "em andamento"' \
     $TASKS_FILE > ${TASKS_FILE}.tmp && mv ${TASKS_FILE}.tmp $TASKS_FILE
  
  send_to_manus "status" "Iniciando tarefa $task_id: $task_desc"
  bash $COMM_SCRIPT tarefa "$task_id"
}

# Função para completar uma tarefa
complete_task() {
  local task_id=$1
  
  # Atualizar estado da tarefa para 'concluída'
  jq --arg id "$task_id" \
     '(.sprints[] | .tarefas[] | select(.id == $id)).status = "concluída"' \
     $TASKS_FILE > ${TASKS_FILE}.tmp && mv ${TASKS_FILE}.tmp $TASKS_FILE
  
  send_to_manus "status" "Tarefa $task_id concluída com sucesso"
}

# Função para iniciar uma sprint
execute_sprint() {
  local sprint_id=$1
  local sprint_info=$(jq --arg id "$sprint_id" '.sprints[] | select(.id == ($id | tonumber))' $TASKS_FILE)
  
  if [ -z "$sprint_info" ]; then
    send_to_manus "error" "Sprint $sprint_id não encontrada"
    return 1
  fi
  
  local sprint_name=$(echo "$sprint_info" | jq -r '.nome')
  
  # Atualizar estado da sprint para 'em andamento'
  jq --arg id "$sprint_id" \
     '(.sprints[] | select(.id == ($id | tonumber))).status = "em andamento"' \
     $TASKS_FILE > ${TASKS_FILE}.tmp && mv ${TASKS_FILE}.tmp $TASKS_FILE
  
  send_to_manus "status" "Iniciando sprint $sprint_id: $sprint_name"
  bash $COMM_SCRIPT sprint "$sprint_id"
}

# Função para enviar status do projeto
send_status() {
  local current_task=$(bash $COMM_SCRIPT status | grep "Tarefa atual" | cut -d ":" -f2 | xargs)
  local current_sprint=$(bash $COMM_SCRIPT status | grep "Sprint atual" | cut -d ":" -f2 | xargs)
  
  if [ "$current_task" != "Nenhuma" ]; then
    local task_info=$(jq --arg id "$current_task" '.sprints[].tarefas[] | select(.id == $id)' $TASKS_FILE)
    local task_desc=$(echo "$task_info" | jq -r '.descricao')
    local task_status=$(echo "$task_info" | jq -r '.status')
    
    send_to_manus "status" "Tarefa atual ($current_task): $task_desc - Status: $task_status"
  fi
  
  if [ "$current_sprint" != "Nenhuma" ]; then
    local sprint_info=$(jq --arg id "$current_sprint" '.sprints[] | select(.id == ($id | tonumber))' $TASKS_FILE)
    local sprint_name=$(echo "$sprint_info" | jq -r '.nome')
    local sprint_status=$(echo "$sprint_info" | jq -r '.status')
    
    send_to_manus "status" "Sprint atual ($current_sprint): $sprint_name - Status: $sprint_status"
  fi
  
  if [ "$current_task" == "Nenhuma" ] && [ "$current_sprint" == "Nenhuma" ]; then
    send_to_manus "status" "Nenhuma tarefa ou sprint em andamento"
  fi
}

# Função para mostrar ajuda
show_help() {
  echo "=== Comandos Cursor ==="
  echo "Uso: ./cursor-command.sh [comando] [parâmetros]"
  echo ""
  echo "Comandos:"
  echo "  responder [tipo] [conteúdo] - Envia resposta para Manus"
  echo "  ler - Lê comandos de Manus"
  echo "  executar [id] - Executa uma tarefa"
  echo "  concluir [id] - Marca uma tarefa como concluída"
  echo "  sprint [id] - Inicia uma sprint"
  echo "  status - Envia status atual para Manus"
  echo "  ajuda - Mostra esta mensagem de ajuda"
}

# Processamento principal
case "$1" in
  "responder")
    send_to_manus "$2" "$3" "$4"
    ;;
  "ler")
    read_from_manus
    ;;
  "executar")
    execute_task "$2"
    ;;
  "concluir")
    complete_task "$2"
    ;;
  "sprint")
    execute_sprint "$2"
    ;;
  "status")
    send_status
    ;;
  "ajuda"|*)
    show_help
    ;;
esac 