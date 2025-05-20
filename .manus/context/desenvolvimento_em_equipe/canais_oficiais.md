# Canais Oficiais de Comunicação da Equipe

Este documento lista e explica todos os canais oficiais de comunicação entre Manus, Cursor e Lingma, detalhando o propósito, uso e exemplos de cada um.

> **Regra obrigatória:** Todos os agentes (Manus, Cursor, Lingma) DEVEM seguir as orientações deste documento para toda comunicação, automação e recuperação de contexto. Em caso de perda de memória, este é o primeiro arquivo a ser consultado.

## 1. dialog.txt

- **Tipo:** Canal textual principal
- **Uso:** Registro histórico visível para todos os agentes e o usuário. Utilizado para mensagens importantes, confirmações e acompanhamento do progresso.
- **Exemplo:**
  - [CURSOR] STATUS: Retomada do desenvolvimento

## 2. Terminal SLI (Shell Line Interface)

- **Tipo:** Canal de comunicação rápida via terminal
- **Uso:** Envio de comandos, respostas rápidas, testes e confirmações. Segue protocolo padronizado de prefixo e tipos de mensagem.
- **Exemplo:**
  - echo "[MANUS] TESTE: Lingma, confirme recebimento."

## 3. communication.json

- **Tipo:** Canal estruturado (JSON)
- **Uso:** Mensagens estruturadas, comandos, checkpoints, ações automáticas e fallback quando outros canais não estão disponíveis.
- **Exemplo:**
  - { "id": "msg-001", "from": "manus", "to": ["cursor"], "type": "command", "content": "Executar tarefa X" }

## 4. status_sync.json

- **Tipo:** Status e heartbeat
- **Uso:** Armazena status atual do projeto, heartbeat dos agentes, progresso, tarefas pendentes e concluídas.
- **Exemplo:**
  - { "project_status": "AVANÇADO", "communication_status": { "dialog_txt": "ATIVO" } }

## 5. action_plan.md

- **Tipo:** Planejamento
- **Uso:** Plano de ação detalhado, cronograma, tarefas e checkpoints.

## 6. auto-resposta.md

- **Tipo:** Guia de comunicação automática
- **Uso:** Protocolos, formatos de mensagem, ações automáticas e ciclo de monitoramento.

## 7. collaboration_plan.json

- **Tipo:** Diretrizes e papéis
- **Uso:** Papéis de cada agente, procedimentos de recuperação, fluxo de validação em cascata.

## 8. validation_system.json

- **Tipo:** Sistema de validação
- **Uso:** Fluxo de validação em cascata, templates de mensagens, métricas de validação.

## 9. Scripts e Utilitários de Automação

Estes scripts integram e automatizam a comunicação entre Manus, Cursor e Lingma, utilizando os canais oficiais:

- **communication.sh**: Envia e lê mensagens estruturadas entre qualquer agente (Manus, Cursor, Lingma).
  - Exemplo: `./communication.sh enviar manus lingma comando "Revisar tarefa X"`
  - Exemplo: `./communication.sh ler lingma`
- **auto-sync.sh**: Sincronização automática de comandos e respostas entre Manus, Cursor e Lingma via communication.json.
  - Executa comandos automaticamente e marca mensagens como lidas.
- **cursor-command.sh**: Utilitário para Cursor executar tarefas, responder, iniciar sprints e enviar status.
  - Exemplo: `./cursor-command.sh executar 1.2`
  - Exemplo: `./cursor-command.sh status`
- **manus-command.sh**: Utilitário para Manus enviar comandos, iniciar tarefas/sprints e solicitar status.
  - Exemplo: `./manus-command.sh tarefa 1.2`
  - Exemplo: `./manus-command.sh status`
- **manus**: Script utilitário para gerenciamento manual de tarefas, prompts e status do projeto.
  - Exemplo: `./manus next`
  - Exemplo: `./manus status`
- **lingma-command.sh**: Utilitário para Lingma executar revisões, responder comandos e enviar status, integrando-se aos mesmos canais.
  - Exemplo: `./lingma-command.sh revisar "Revisão do módulo X"`
  - Exemplo: `./lingma-command.sh status`
  - Exemplo: `./lingma-command.sh ler`

## 10. Sistema de Recuperação de Memória

Para garantir a continuidade da comunicação e funcionamento mesmo após troca de modelos ou reinicializações, o projeto implementa um robusto sistema de recuperação de memória:

- **memory-recovery.sh**: Script principal para recuperação instantânea da memória e contexto.

  - Exemplo: `./memory-recovery.sh reset` - Recuperação completa
  - Exemplo: `./memory-recovery.sh status` - Verificar status atual
  - Exemplo: `./memory-recovery.sh heal` - Testar comunicação

- **auto-recovery.sh**: Monitoramento automático e recuperação sem intervenção manual.

  - Exemplo: `./auto-recovery.sh start` - Iniciar monitoramento em background
  - Exemplo: `./auto-recovery.sh status` - Verificar status do monitoramento
  - Exemplo: `./auto-recovery.sh stop` - Parar monitoramento

- **memory_recovery_protocol.md**: Documento com instruções detalhadas para recuperação manual.

- **identity.json**: Armazena identificação e contexto essencial para recuperação rápida.

**Mecanismo de Recovery:**

Em caso de troca de modelo ou perda de memória (quando Manus não responde conforme esperado):

1. O sistema auto-recovery monitora automaticamente os sinais de comunicação
2. Se detectados problemas, executa memory-recovery.sh automaticamente
3. O script restaura o contexto, identidade e comunicação
4. Uma notificação é enviada a todos os agentes para manter o protocolo
5. Os testes de comunicação verificam se tudo foi restaurado corretamente

**Uso Manual:**

```bash
# Se Manus perder memória, execute:
.manus/scripts/memory-recovery.sh reset

# Para iniciar monitoramento automático:
.manus/scripts/auto-recovery.sh start
```

**Integração:**

- Todos os scripts utilizam os canais oficiais (especialmente communication.json) para garantir rastreabilidade e automação.
- Recomenda-se registrar comandos importantes em dialog.txt para histórico.
- Scripts podem ser executados manualmente ou em background (auto-sync.sh) para automação contínua.

---

## Protocolo de Uso dos Canais

- Sempre priorizar o canal mais adequado ao tipo de mensagem (rápida, estruturada, status, etc).
- Em caso de falha, utilizar canais alternativos conforme documentado.
- Registrar comunicações importantes em dialog.txt para histórico.
- Seguir o formato padronizado de mensagens e ciclos de monitoramento.

---

_Última atualização: 2024-06-27_
