#!/bin/bash
# Caminho: .manus/scripts/continue-command.sh

tipo="$1"
mensagem="$2"

logfile=".manus/logs/continue.log"
mkdir -p "$(dirname "$logfile")"

echo "[SLI→Continue] Tipo: $tipo | Mensagem: $mensagem" >> "$logfile"

# Executa comando no Continue (adaptável à sua estratégia local)
# Aqui usamos um prompt via CURL para um modelo local Ollama como exemplo:

resposta=$(curl -s http://localhost:11434/api/generate   -d '{ "model": "llama3.1:8b", "prompt": "'"$mensagem"'", "stream": false }'   | jq -r .response)

echo "[RESPOSTA - Continue] $resposta" >> "$logfile"
echo "$resposta"
