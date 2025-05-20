#!/bin/bash

# Script de comunicação entre Manus, Cursor e Lingma
# Uso: ./communication.sh [comando] [parâmetros]

COMM_FILE=".manus/context/communication.json"
TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%S")

# Criar arquivo de comunicação se não existir
if [ ! -f "$COMM_FILE" ]; then
  mkdir -p "$(dirname "$COMM_FILE")"
  cat > "$COMM_FILE" <<EOF
{
  "messages": [],
  "channel": {
    "name": "communication_json",
    "status": "ATIVO",
    "last_update": "$TIMESTAMP"
  },
  "current_task": "",
  "current_sprint": ""
}
EOF
  echo "Arquivo de comunicação criado: $COMM_FILE"
fi

# Função para enviar mensagem
send_message() {
  local from=$1
  local to=$2
  local type=$3
  local content=$4
  local requires_response=${5:-false}
  
  # Gerar ID único para a mensagem
  local msg_id="msg-$(date +%s)-$(shuf -i 1000-9999 -n 1)"
  
  # Adicionar nova mensagem ao arquivo
  jq --arg id "$msg_id" \
     --arg timestamp "$TIMESTAMP" \
     --arg from "$from" \
     --arg to "$to" \
     --arg type "$type" \
     --arg content "$content" \
     --arg requires_response "$requires_response" \
     '.messages += [{"id": $id, "timestamp": $timestamp, "from": $from, "to": $to, "type": $type, "content": $content, "requires_response": ($requires_response == "true")}] | .channel.last_update = $timestamp' \
     "$COMM_FILE" > "${COMM_FILE}.tmp" && mv "${COMM_FILE}.tmp" "$COMM_FILE"
  
  echo "Mensagem enviada: $from -> $to ($type): $content"
  
  # Registrar no dialog.txt para mensagens importantes
  if [ "$type" = "alerta" ] || [ "$type" = "urgente" ] || [ "$type" = "teste" ]; then
    echo "[$from] ${type^^}: $content" >> dialog.txt
  fi
}

# Função para ler mensagens não lidas
read_messages() {
  local recipient=$1
  
  if [ ! -s "$COMM_FILE" ]; then
    echo "Arquivo de comunicação vazio ou não existente."
    return 1
  fi
  
  # Verificar se há mensagens não lidas
  local unread_count=$(jq --arg recipient "$recipient" '.messages | map(select(.to == $recipient and .read != true)) | length' "$COMM_FILE")
  
  if [ "$unread_count" -eq 0 ]; then
    echo "Nenhuma mensagem não lida para $recipient"
    return 0
  fi
  
  # Mostrar mensagens não lidas
  echo "=== Mensagens não lidas para $recipient: $unread_count ==="
  jq -r --arg recipient "$recipient" '.messages[] | select(.to == $recipient and .read != true) | "\(.timestamp) - \(.from): [\(.type)] \(.content)"' "$COMM_FILE"
    
  # Marcar mensagens como lidas
  jq --arg recipient "$recipient" \
     '.messages = [.messages[] | if .to == $recipient then . + {"read": true} else . end]' \
     "$COMM_FILE" > "${COMM_FILE}.tmp" && mv "${COMM_FILE}.tmp" "$COMM_FILE"
  
  return 0
}

# Função para definir tarefa atual
set_current_task() {
  local task_id=$1
  
  jq --arg task_id "$task_id" '.current_task = $task_id' "$COMM_FILE" > "${COMM_FILE}.tmp" && mv "${COMM_FILE}.tmp" "$COMM_FILE"
  echo "Tarefa atual definida para: $task_id"
}

# Função para definir sprint atual
set_current_sprint() {
  local sprint_id=$1
  
  jq --arg sprint_id "$sprint_id" '.current_sprint = $sprint_id' "$COMM_FILE" > "${COMM_FILE}.tmp" && mv "${COMM_FILE}.tmp" "$COMM_FILE"
  echo "Sprint atual definida para: $sprint_id"
}

# Função para mostrar ajuda
show_help() {
  echo "=== Sistema de Comunicação Manus-Cursor-Lingma ==="
  echo "Uso: ./communication.sh [comando] [parâmetros]"
  echo ""
  echo "Comandos:"
  echo "  enviar [de] [para] [tipo] [conteúdo] [requer_resposta] - Envia uma mensagem"
  echo "    [de]/[para]: manus, cursor, lingma, sistema"
  echo "    [tipo]: info, alerta, urgente, comando, resposta, teste, status"
  echo "    [conteúdo]: Texto da mensagem (entre aspas)"
  echo "    [requer_resposta]: true ou false (opcional, padrão: false)"
  echo "  ler [destinatário] - Lê mensagens não lidas (manus, cursor ou lingma)"
  echo "  tarefa [id] - Define a tarefa atual"
  echo "  sprint [id] - Define a sprint atual"
  echo "  status - Mostra status atual da comunicação"
  echo "  ajuda - Mostra esta mensagem de ajuda"
}

# Função para mostrar status
show_status() {
  if [ ! -f "$COMM_FILE" ]; then
    echo "Arquivo de comunicação não encontrado: $COMM_FILE"
    return 1
  fi
  
  local current_task=$(jq -r '.current_task' "$COMM_FILE")
  local current_sprint=$(jq -r '.current_sprint' "$COMM_FILE")
  local last_update=$(jq -r '.channel.last_update' "$COMM_FILE")
  local message_count=$(jq '.messages | length' "$COMM_FILE")
  
  echo "=== Status da Comunicação ==="
  echo "Último update: $last_update"
  echo "Sprint atual: ${current_sprint:-Nenhuma}"
  echo "Tarefa atual: ${current_task:-Nenhuma}"
  echo "Total de mensagens: $message_count"
  echo "Canal: $(jq -r '.channel.name' "$COMM_FILE") ($(jq -r '.channel.status' "$COMM_FILE"))"
}

# Processamento principal
case "$1" in
  "enviar")
    if [ $# -lt 5 ]; then
      echo "Erro: Parâmetros insuficientes para enviar mensagem."
      echo "Uso: ./communication.sh enviar [de] [para] [tipo] [conteúdo] [requer_resposta]"
      exit 1
    fi
    send_message "$2" "$3" "$4" "$5" "${6:-false}"
    ;;
  "ler")
    if [ -z "$2" ]; then
      echo "Erro: Destinatário não especificado."
      echo "Uso: ./communication.sh ler [destinatário]"
      exit 1
    fi
    read_messages "$2"
    ;;
  "tarefa")
    if [ -z "$2" ]; then
      echo "Erro: ID da tarefa não especificado."
      echo "Uso: ./communication.sh tarefa [id]"
      exit 1
    fi
    set_current_task "$2"
    ;;
  "sprint")
    if [ -z "$2" ]; then
      echo "Erro: ID da sprint não especificado."
      echo "Uso: ./communication.sh sprint [id]"
      exit 1
    fi
    set_current_sprint "$2"
    ;;
  "status")
    show_status
    ;;
  "ajuda"|*)
    show_help
    ;;
esac 