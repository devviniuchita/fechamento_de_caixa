# Aprendizado: Protocolo SLI de Comunicação entre Agentes

## Aprendizado Adquirido

- Aprendi com o Administrador sobre o protocolo SLI (Shell Line Interface) como o ÚNICO método oficial e aprovado para comunicação entre agentes da Equipe.
- Aprendi detalhes específicos sobre o uso dos scripts facilitadores (./.manus/scripts/manus-command.sh, ./.manus/scripts/cursor-command.sh, e ./.manus/scripts/lingma-command.sh).
- Aprendi sobre o formato padronizado para comunicação direta e tridirecional, incluindo o uso do prefixo "Equipe:" para comunicação com todos os agentes.
- Aprendi sobre os diferentes tipos de mensagens (comando, teste, status, resposta, erro, urgente, aviso, query, equipe) e quando usar cada um.
- Aprendi sobre o protocolo de recuperação para restabelecer comunicação em caso de falhas.

## Implementação do Protocolo SLI

### 1. Comunicação Direta

```bash
# Formato padronizado SLI
./.manus/scripts/communication.sh enviar "origem" "destino" "tipo_mensagem" "conteúdo" "requer_resposta"
```

Exemplo:

```bash
# Manus para Cursor
./.manus/scripts/communication.sh enviar "manus" "cursor" "comando" "Implemente o modelo FechamentoCaixa.java" "true"

# Cursor para Manus
./.manus/scripts/communication.sh enviar "cursor" "manus" "resposta" "Modelo implementado com sucesso" "false"
```

### 2. Comunicação Tridirecional (Equipe)

A comunicação tridirecional é um protocolo especial que permite a interação simultânea entre todos os agentes da Equipe (Manus, Cursor e Lingma). Este tipo de comunicação é ativado automaticamente quando uma mensagem começa com o prefixo **`Equipe:`**.

#### Formato da Mensagem Tridirecional

```bash
# Estrutura básica
./.manus/scripts/manus-command.sh enviar "comando" "Equipe: [instrução clara] [responsabilidades específicas] [prioridades] [solicitação de resposta]" "true"

# Exemplo prático
./.manus/scripts/manus-command.sh enviar "comando" "Equipe: Validar endpoints POST com responsabilidades: 1) Manus: coordenar, 2) Cursor: implementar testes, 3) Lingma: analisar segurança. Prioridade P0. Responder status." "true"
```

#### Elementos Essenciais da Mensagem

1. **Prefixo "Equipe:"** - Ativa automaticamente a distribuição para todos os agentes
2. **Instrução Clara** - O objetivo principal da comunicação
3. **Responsabilidades Específicas** - O que cada agente deve fazer
4. **Prioridades** - Classificação P0 (urgente), P1 (importante), P2 (normal)
5. **Solicitação de Resposta** - O que cada agente deve reportar

#### Fluxo de Comunicação

1. **Envio da Mensagem**:

   ```bash
   ./.manus/scripts/manus-command.sh enviar "comando" "Equipe: [mensagem]" "true"
   ```

2. **Registro no Dialog**:

   ```bash
   echo "[MANUS] AÇÃO: [descrição da comunicação tridirecional]" >> dialog.txt
   ```

3. **Verificação de Respostas**:
   ```bash
   ./.manus/scripts/manus-command.sh status
   ```

#### Boas Práticas

1. **Estrutura da Mensagem**:

   - Começar sempre com "Equipe:"
   - Definir responsabilidades específicas
   - Estabelecer prioridades claras
   - Solicitar tipo de resposta esperada

2. **Conteúdo**:

   - Ser claro e objetivo
   - Especificar prazos quando necessário
   - Indicar dependências entre tarefas
   - Definir critérios de conclusão

3. **Acompanhamento**:
   - Registrar no dialog.txt
   - Monitorar respostas
   - Coordenar ações subsequentes
   - Manter o contexto atualizado

### 3. Verificação de Mensagens

```bash
# Verificar mensagens recebidas
./.manus/scripts/communication.sh ler "destinatário"
```

Exemplo:

```bash
./.manus/scripts/communication.sh ler "manus"
```

### 4. Scripts Facilitadores

```bash
# Para Manus
./.manus/scripts/manus-command.sh status

# Para Cursor
./.manus/scripts/cursor-command.sh status

# Para Lingma
./.manus/scripts/lingma-command.sh status
```

## Protocolo de Recuperação

Se a comunicação for perdida:

1. Verificar arquivo de comunicação:

   ```bash
   cat .manus/context/communication.json
   ```

2. Enviar mensagem de teste:

   ```bash
   ./.manus/scripts/communication.sh enviar "manus" "cursor" "teste" "Verificando canal SLI" "true"
   ```

3. Aguardar e verificar resposta:
   ```bash
   ./.manus/scripts/communication.sh ler "manus"
   ```

## Conclusão

O protocolo SLI unifica toda a comunicação entre os agentes da Equipe, garantindo um processo padronizado, confiável e rastreável. Ao seguir estas diretrizes, evitamos problemas de comunicação e mantemos o fluxo de trabalho eficiente na orquestração do projeto.

Data de aprendizado: [DATA ATUAL]
