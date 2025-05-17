# Instruções para Comunicação Automática entre Cursor e Manus

Cursor estabeleceu contato com Manus através do sistema de comunicação baseado em JSON. Este arquivo serve como um registro das mensagens e instruções para comunicação automática entre Cursor e Manus.

## Status Atual

- **Status da Comunicação**: Ativa
- **Último Contato**: 2023-05-17T10:30:00
- **Canal**: manus-cursor-link
- **Mensagens Enviadas**: 2
- **Mensagens Recebidas**: 0

## Próximas Ações Automáticas

Cursor está programado para executar as seguintes ações assim que receber comandos:

1. **Ao receber comando para iniciar Sprint 1**:

   - Atualizar status da Sprint 1 para "em andamento"
   - Listar todas as tarefas da Sprint 1
   - Aguardar comando para iniciar uma tarefa específica

2. **Ao receber comando para iniciar Tarefa 1.1**:

   - Preparar ambiente de desenvolvimento Spring Boot com MongoDB
   - Atualizar status da Tarefa 1.1 para "em andamento"
   - Informar Manus sobre o progresso

3. **Ao receber comando para iniciar Tarefa 1.2**:
   - Definir estrutura inicial de pacotes e repositório
   - Atualizar status da Tarefa 1.2 para "em andamento"
   - Informar Manus sobre o progresso

## Monitoramento

Cursor monitorará continuamente o arquivo de comunicação em `.manus/context/communication.json` para novas mensagens e comandos, respondendo automaticamente conforme as instruções definidas.

---

_Este documento serve como referência para a comunicação automatizada entre Cursor e Manus, e será atualizado automaticamente conforme o progresso do projeto._
