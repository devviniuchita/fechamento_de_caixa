#!/bin/bash

# Script de comunicação entre Manus e Cursor
# Uso: ./communication.sh [remetente] [destinatário] [tipo] [conteúdo]

COMM_FILE=".manus/context/communication.json"
TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%S")

# Função para enviar mensagem
send_message() {
  local from=$1
  local to=$2
  local type=$3
  local content=$4
  local requires_response=${5:-false}
  
  # Obter o próximo ID de mensagem
  local last_id=$(jq '.messages | max_by(.id) | .id' $COMM_FILE)
  local next_id=$((last_id + 1))
  
  # Adicionar nova mensagem ao arquivo
  jq --arg id "$next_id" \
     --arg timestamp "$TIMESTAMP" \
     --arg from "$from" \
     --arg to "$to" \
     --arg type "$type" \
     --arg content "$content" \
     --arg requires_response "$requires_response" \
     '.messages += [{"id": $id, "timestamp": $timestamp, "from": $from, "to": $to, "type": $type, "content": $content, "requires_response": ($requires_response == "true")}] | .channel.last_update = $timestamp' \
     $COMM_FILE > ${COMM_FILE}.tmp && mv ${COMM_FILE}.tmp $COMM_FILE
  
  echo "Mensagem enviada: $from -> $to ($type): $content"
}

# Função para ler mensagens não lidas
read_messages() {
  local recipient=$1
  local messages=$(jq --arg recipient "$recipient" '.messages[] | select(.to == $recipient and .read != true)' $COMM_FILE)
  
  if [ -z "$messages" ]; then
    echo "Nenhuma mensagem não lida para $recipient"
  else
    echo "$messages"
    
    # Marcar mensagens como lidas
    jq --arg recipient "$recipient" \
       '.messages = [.messages[] | if .to == $recipient then . + {"read": true} else . end]' \
       $COMM_FILE > ${COMM_FILE}.tmp && mv ${COMM_FILE}.tmp $COMM_FILE
  fi
}

# Função para definir tarefa atual
set_current_task() {
  local task_id=$1
  
  jq --arg task_id "$task_id" '.current_task = $task_id' $COMM_FILE > ${COMM_FILE}.tmp && mv ${COMM_FILE}.tmp $COMM_FILE
  echo "Tarefa atual definida para: $task_id"
}

# Função para definir sprint atual
set_current_sprint() {
  local sprint_id=$1
  
  jq --arg sprint_id "$sprint_id" '.current_sprint = $sprint_id' $COMM_FILE > ${COMM_FILE}.tmp && mv ${COMM_FILE}.tmp $COMM_FILE
  echo "Sprint atual definida para: $sprint_id"
}

# Função para mostrar ajuda
show_help() {
  echo "=== Sistema de Comunicação Manus-Cursor ==="
  echo "Uso: ./communication.sh [comando] [parâmetros]"
  echo ""
  echo "Comandos:"
  echo "  enviar [para] [tipo] [conteúdo] - Envia uma mensagem"
  echo "  ler [destinatário] - Lê mensagens não lidas"
  echo "  tarefa [id] - Define a tarefa atual"
  echo "  sprint [id] - Define a sprint atual"
  echo "  status - Mostra status atual da comunicação"
  echo "  ajuda - Mostra esta mensagem de ajuda"
}

# Função para mostrar status
show_status() {
  local current_task=$(jq -r '.current_task' $COMM_FILE)
  local current_sprint=$(jq -r '.current_sprint' $COMM_FILE)
  local last_update=$(jq -r '.channel.last_update' $COMM_FILE)
  
  echo "=== Status da Comunicação ==="
  echo "Último update: $last_update"
  echo "Sprint atual: ${current_sprint:-Nenhuma}"
  echo "Tarefa atual: ${current_task:-Nenhuma}"
  echo "Canal: $(jq -r '.channel.name' $COMM_FILE) ($(jq -r '.channel.status' $COMM_FILE))"
}

# Processamento principal
case "$1" in
  "enviar")
    send_message "$2" "$3" "$4" "$5" "$6"
    ;;
  "ler")
    read_messages "$2"
    ;;
  "tarefa")
    set_current_task "$2"
    ;;
  "sprint")
    set_current_sprint "$2"
    ;;
  "status")
    show_status
    ;;
  "ajuda"|*)
    show_help
    ;;
esac 