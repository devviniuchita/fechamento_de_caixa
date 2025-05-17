#!/bin/bash

# Script de sincronização automática entre Manus e Cursor
# Este script monitora a comunicação e executa comandos automaticamente

COMM_FILE=".manus/context/communication.json"
MANUS_SCRIPT=".manus/scripts/manus-command.sh"
CURSOR_SCRIPT=".manus/scripts/cursor-command.sh"
INTERVAL=5  # Intervalo de verificação em segundos

echo "Iniciando sistema de sincronização automática Manus-Cursor..."
echo "Pressione Ctrl+C para encerrar"

# Função para processar comandos de Manus para Cursor
process_manus_commands() {
  local messages=$(jq -c '.messages[] | select(.from == "manus" and .to == "cursor" and .read != true)' $COMM_FILE)
  
  if [ ! -z "$messages" ]; then
    echo "$messages" | while read -r message; do
      local msg_id=$(echo "$message" | jq -r '.id')
      local msg_type=$(echo "$message" | jq -r '.type')
      local msg_content=$(echo "$message" | jq -r '.content')
      
      echo "Processando comando de Manus: [$msg_type] $msg_content"
      
      case "$msg_type" in
        "command")
          if [[ $msg_content == "Iniciar tarefa"* ]]; then
            local task_id=$(echo "$msg_content" | sed 's/Iniciar tarefa //')
            bash $CURSOR_SCRIPT executar "$task_id"
          elif [[ $msg_content == "Iniciar sprint"* ]]; then
            local sprint_id=$(echo "$msg_content" | sed 's/Iniciar sprint //')
            bash $CURSOR_SCRIPT sprint "$sprint_id"
          fi
          ;;
        "query")
          if [[ $msg_content == "status" ]]; then
            bash $CURSOR_SCRIPT status
          fi
          ;;
      esac
      
      # Marcar mensagem como lida
      jq --arg id "$msg_id" \
         '.messages = [.messages[] | if .id == $id then . + {"read": true} else . end]' \
         $COMM_FILE > ${COMM_FILE}.tmp && mv ${COMM_FILE}.tmp $COMM_FILE
    done
  fi
}

# Função para processar respostas de Cursor para Manus
process_cursor_responses() {
  local messages=$(jq -c '.messages[] | select(.from == "cursor" and .to == "manus" and .read != true)' $COMM_FILE)
  
  if [ ! -z "$messages" ]; then
    echo "$messages" | while read -r message; do
      local msg_id=$(echo "$message" | jq -r '.id')
      local msg_type=$(echo "$message" | jq -r '.type')
      local msg_content=$(echo "$message" | jq -r '.content')
      
      echo "Processando resposta de Cursor: [$msg_type] $msg_content"
      
      # Aqui poderia ter lógica para Manus responder automaticamente
      # Por enquanto, apenas marca a mensagem como lida
      
      # Marcar mensagem como lida
      jq --arg id "$msg_id" \
         '.messages = [.messages[] | if .id == $id then . + {"read": true} else . end]' \
         $COMM_FILE > ${COMM_FILE}.tmp && mv ${COMM_FILE}.tmp $COMM_FILE
    done
  fi
}

# Loop principal
while true; do
  process_manus_commands
  process_cursor_responses
  sleep $INTERVAL
done 