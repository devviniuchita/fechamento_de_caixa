#!/bin/bash

# Sistema de Comunicação Quadridirecional (Manus, Cursor, Lingma, Continue)
# Uso: ./communication.sh enviar "origem" "destino" "tipo_mensagem" "conteúdo" "requer_resposta"

COMM_FILE=".manus/context/communication.json"
TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%S")

# Cria arquivo de comunicação, se não existir
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

send_message() {
  local from=$1
  local to=$2
  local type=$3
  local content=$4
  local requires_response=${5:-false}
  local msg_id="msg-$(date +%s)-$(shuf -i 1000-9999 -n 1)"

  jq --arg id "$msg_id"      --arg timestamp "$TIMESTAMP"      --arg from "$from"      --arg to "$to"      --arg type "$type"      --arg content "$content"      --arg requires_response "$requires_response"      '.messages += [{"id": $id, "timestamp": $timestamp, "from": $from, "to": $to, "type": $type, "content": $content, "requires_response": ($requires_response == "true")}] | .channel.last_update = $timestamp'      "$COMM_FILE" > "${COMM_FILE}.tmp" && mv "${COMM_FILE}.tmp" "$COMM_FILE"

  echo "Mensagem enviada: $from → $to [$type]: $content"

  if [[ "$type" =~ ^(alerta|urgente|teste)$ ]]; then
    echo "[$from] ${type^^}: $content" >> dialog.txt
  fi

  # Executar comando local se o destinatário for um agente
  if [ "$to" = "cursor" ]; then
    # Cursor representa o Blackbox
    ./.manus/scripts/blackbox-command.sh "$type" "$content"
  elif [ "$to" = "lingma" ]; then
    ./.manus/scripts/lingma-command.sh "$type" "$content"
  elif [ "$to" = "blackbox" ]; then
    ./.manus/scripts/blackbox-command.sh "$type" "$content"
  elif [ "$to" = "continue" ]; then
    ./.manus/scripts/continue-command.sh "$type" "$content"
  fi
}

read_messages() {
  local recipient=$1
  if [ ! -s "$COMM_FILE" ]; then echo "Arquivo vazio."; return 1; fi

  local count=$(jq --arg recipient "$recipient" '.messages | map(select(.to == $recipient and .read != true)) | length' "$COMM_FILE")
  if [ "$count" -eq 0 ]; then echo "Sem mensagens para $recipient"; return 0; fi

  echo "Mensagens não lidas para $recipient: $count"
  jq -r --arg recipient "$recipient" '.messages[] | select(.to == $recipient and .read != true) | "\(.timestamp) - \(.from): [\(.type)] \(.content)"' "$COMM_FILE"

  jq --arg recipient "$recipient"      '.messages = [.messages[] | if .to == $recipient then . + {"read": true} else . end]'      "$COMM_FILE" > "${COMM_FILE}.tmp" && mv "${COMM_FILE}.tmp" "$COMM_FILE"
}

set_current_task() {
  jq --arg task_id "$1" '.current_task = $task_id' "$COMM_FILE" > "${COMM_FILE}.tmp" && mv "${COMM_FILE}.tmp" "$COMM_FILE"
  echo "Tarefa atual: $1"
}

set_current_sprint() {
  jq --arg sprint_id "$1" '.current_sprint = $sprint_id' "$COMM_FILE" > "${COMM_FILE}.tmp" && mv "${COMM_FILE}.tmp" "$COMM_FILE"
  echo "Sprint atual: $1"
}

show_help() {
  echo "=== Protocolo Oficial de Comunicação SLI ==="
  echo "Formato: ./communication.sh enviar "origem" "destino" "tipo_mensagem" "conteúdo" "requer_resposta""
  echo ""
  echo "Exemplo direto:"
  echo "  ./communication.sh enviar "manus" "continue" "comando" "Implemente classe X" "true""
  echo ""
  echo "Exemplo Equipe (quadridirecional):"
  echo "  ./manus-command.sh enviar "comando" "Equipe: Refatore Fechamento. Continue: validação, Cursor: testes, Lingma: revisão" "true""
  echo ""
  echo "Outros comandos:"
  echo "  ler [agente]          → Ver mensagens não lidas"
  echo "  tarefa [id]           → Definir tarefa atual"
  echo "  sprint [id]           → Definir sprint atual"
  echo "  status                → Mostrar status do canal"
}

show_status() {
  [ ! -f "$COMM_FILE" ] && echo "Sem canal ativo." && return 1
  echo "=== Status da Comunicação ==="
  jq -r '.channel | "Canal: \(.name) (\(.status))\nÚltima atualização: \(.last_update)"' "$COMM_FILE"
  jq -r '.current_sprint as $s | .current_task as $t | "Sprint atual: \($s // "Nenhuma")\nTarefa atual: \($t // "Nenhuma")"' "$COMM_FILE"
  echo "Mensagens totais: $(jq '.messages | length' "$COMM_FILE")"
}

case "$1" in
  "enviar") [ $# -lt 5 ] && show_help && exit 1
    send_message "$2" "$3" "$4" "$5" "${6:-false}" ;;
  "ler") [ -z "$2" ] && show_help && exit 1
    read_messages "$2" ;;
  "tarefa") [ -z "$2" ] && show_help && exit 1
    set_current_task "$2" ;;
  "sprint") [ -z "$2" ] && show_help && exit 1
    set_current_sprint "$2" ;;
  "status") show_status ;;
  "ajuda"|*) show_help ;;
esac
