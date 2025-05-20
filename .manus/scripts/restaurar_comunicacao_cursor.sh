#!/bin/bash

# Script para restauração rápida da comunicação com Cursor após perda de memória
# Uso: ./restaurar_comunicacao_cursor.sh

# Cores para saída
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=======================================================${NC}"
echo -e "${BLUE}  RESTAURAÇÃO DE COMUNICAÇÃO: MANUS ↔ CURSOR${NC}"
echo -e "${BLUE}=======================================================${NC}"

# Verificar se dialog.txt existe, criar se não existir
if [ ! -f "dialog.txt" ]; then
  echo -e "${YELLOW}Arquivo dialog.txt não encontrado. Criando...${NC}"
  touch dialog.txt
  echo -e "${GREEN}✓ Arquivo dialog.txt criado com sucesso${NC}"
else
  echo -e "${GREEN}✓ Arquivo dialog.txt já existe${NC}"
fi

# Enviar mensagem de recuperação para Cursor
echo -e "${YELLOW}Enviando mensagem de recuperação para Cursor...${NC}"
 echo "[MANUS] RECUPERAÇÃO: Cursor, houve perda de memória. Confirme recebimento para reestabelecer comunicação bilateral. $(date)" > dialog.txt
echo -e "${GREEN}✓ Mensagem enviada com sucesso${NC}"

# Instruções para verificar resposta
echo -e "${BLUE}------------------------------------------------------${NC}"
echo -e "${YELLOW}Para verificar resposta do Cursor, execute:${NC}"
echo -e "  cat dialog.txt"
echo -e "${BLUE}------------------------------------------------------${NC}"

# Lembrete do protocolo de comunicação
echo -e "${YELLOW}LEMBRETE IMPORTANTE:${NC}"
echo -e "1. SEMPRE adicione um espaço antes de qualquer comando no terminal"
echo -e "   ✓ CORRETO:  ${GREEN} echo \"[MANUS] COMANDO: texto\"${NC}"
echo -e "   ✗ INCORRETO:${RED}echo \"[MANUS] COMANDO: texto\"${NC} (sem espaço inicial)"
echo -e ""
echo -e "2. Use o formato padronizado para todas as mensagens:"
echo -e "   ${GREEN}[ORIGEM] TIPO_MENSAGEM: Conteúdo${NC}"
echo -e ""
echo -e "3. Para comunicação completa, consulte:"
echo -e "   ${BLUE}.manus/context/comunicacao_oficial_manus_cursor.md${NC}"

# Verificar se houve resposta após 5 segundos
echo -e "${YELLOW}Aguardando resposta do Cursor (5 segundos)...${NC}"
sleep 5

# Verificar o conteúdo atual do dialog.txt
if grep -q "\[CURSOR\] CONFIRMAÇÃO:" dialog.txt; then
  echo -e "${GREEN}✓ COMUNICAÇÃO RESTABELECIDA! Cursor respondeu:${NC}"
  grep "\[CURSOR\]" dialog.txt
  echo -e "${BLUE}=======================================================${NC}"
  echo -e "${GREEN}Comunicação bilateral Manus ↔ Cursor está ativa!${NC}"
  echo -e "${BLUE}=======================================================${NC}"
  exit 0
else
  echo -e "${YELLOW}Nenhuma resposta detectada nos primeiros 5 segundos.${NC}"
  echo -e "${YELLOW}Verifique manualmente com:${NC}"
  echo -e "  cat dialog.txt"
  echo -e "${BLUE}=======================================================${NC}"
  exit 1
fi 