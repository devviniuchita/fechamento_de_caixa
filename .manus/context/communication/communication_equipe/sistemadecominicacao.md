# Sistema de Comunicação Manus-Cursor-Lingma

Este sistema permite a comunicação tridirecional entre Manus (orquestrador), Cursor (executor) e Lingma (executor) através de um mecanismo baseado em arquivo JSON e scripts shell.

## Componentes

- **communication.json**: Arquivo central que armazena mensagens e estado da comunicação
- **communication.sh**: Script principal de comunicação com funções básicas
- **manus-command.sh**: Interface específica para comandos de Manus
- **cursor-command.sh**: Interface específica para respostas e ações do Cursor
- **auto-sync.sh**: Sincronização automática de mensagens entre Manus e Cursor

## Como Usar

### Para Manus (orquestrador)

```bash
# Enviar comando para Cursor
.manus/scripts/manus-command.sh enviar comando "Implementar feature X"

# Ler respostas do Cursor
.manus/scripts/manus-command.sh ler

# Iniciar uma tarefa
.manus/scripts/manus-command.sh tarefa 1.1

# Iniciar uma sprint
.manus/scripts/manus-command.sh sprint 1

# Verificar status atual
.manus/scripts/manus-command.sh status
```

### Para Cursor e Lingma (executores)

```bash
# Enviar resposta para Manus
.manus/scripts/cursor-command.sh responder status "Tarefa concluída com sucesso"

# Ler comandos de Manus
.manus/scripts/cursor-command.sh ler

# Executar uma tarefa
.manus/scripts/cursor-command.sh executar 1.1

# Marcar tarefa como concluída
.manus/scripts/cursor-command.sh concluir 1.1

# Enviar status atual para Manus
.manus/scripts/cursor-command.sh status
```

### Sincronização Automática

Para ativar o processamento automático de mensagens:

```bash
# Iniciar em background
nohup .manus/scripts/auto-sync.sh > .manus/logs/sync.log 2>&1 &

# Verificar se está rodando
ps aux | grep auto-sync
```

## Fluxo de Comunicação

1. Manus envia comando para Cursor via `manus-command.sh`
2. O comando é armazenado em `communication.json`
3. Cursor lê o comando via `cursor-command.sh` (manual) ou via `auto-sync.sh` (automático)
4. Cursor executa a ação solicitada
5. Cursor envia resposta para Manus
6. Manus lê a resposta e decide sobre próximos passos

## Integração com tasks.json

O sistema de comunicação está integrado com o arquivo de tarefas `.manus/tasks.json`, permitindo:

- Iniciar e acompanhar tarefas específicas
- Atualizar status de tarefas
- Gerenciar sprints

## Troubleshooting

- **Permissões**: Certifique-se que todos os scripts têm permissão de execução (`chmod +x *.sh`)
- **Dependências**: O sistema requer `jq` para processamento de JSON
- **Reset**: Para resetar a comunicação, exclua e recrie o arquivo `communication.json`

_Este documento serve como referência para a comunicação automatizada entre Manus, Cursor e Lingma e será atualizado automaticamente._
