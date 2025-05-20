#!/bin/bash

# Script de comandos para Lingma
# Uso: ./lingma-command.sh [comando] [parâmetros]

COMM_SCRIPT=".manus/scripts/communication.sh"

# Função para enviar resposta para Manus
send_to_manus() {
  local response_type=$1
  local content=$2
  local requires_response=${3:-false}
  
  bash $COMM_SCRIPT enviar "lingma" "manus" "$response_type" "$content" "$requires_response"
}

# Função para ler comandos de Manus
read_from_manus() {
  bash $COMM_SCRIPT ler "lingma"
}

# Função para executar revisão (exemplo)
execute_review() {
  local review_desc=$1
  send_to_manus "resposta" "Revisão executada: $review_desc"
}

# Função para enviar status
send_status() {
  send_to_manus "status" "Lingma ativo e monitorando canais"
}

# Função para mostrar ajuda
show_help() {
  echo "=== Comandos Lingma ==="
  echo "Uso: ./lingma-command.sh [comando] [parâmetros]"
  echo ""
  echo "Comandos:"
  echo "  responder [tipo] [conteúdo] - Envia resposta para Manus"
  echo "  ler - Lê comandos de Manus"
  echo "  revisar [desc] - Executa revisão e responde para Manus"
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
  "revisar")
    execute_review "$2"
    ;;
  "status")
    send_status
    ;;
  "ajuda"|*)
    show_help
    ;;
esac 