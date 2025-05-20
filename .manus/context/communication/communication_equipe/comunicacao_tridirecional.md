# Protocolo de Comunicação Tridirecional da Equipe

## Introdução

Este documento define o protocolo oficial para comunicação tridirecional entre todos os agentes da Equipe (Manus, Cursor e Lingma) no projeto Sistema de Fechamento de Caixa. Este protocolo utiliza o prefixo **`Equipe:`** para iniciar comunicações que devem ser processadas e respondidas por todos os membros da Equipe simultaneamente.

## Princípios da Comunicação Tridirecional

1. **Distribuição Universal**: Mensagens são distribuídas para todos os agentes da Equipe
2. **Processamento Paralelo**: Todos os agentes processam a mesma instrução simultaneamente
3. **Respostas Compartilhadas**: Cada agente responde conforme sua especialidade
4. **Coordenação Automática**: O sistema gerencia a distribuição sem intervenção manual

## Como Funciona

A comunicação tridirecional é ativada automaticamente quando uma mensagem começa com **`Equipe:`**, seja em:

1. Comandos do administrador
2. Mensagens SLI enviadas pelo Manus
3. Qualquer comunicação oficial via SLI com destino "equipe"

### Exemplo do Fluxo de Comunicação

```
Administrador → "Equipe: implementar validação de segurança no módulo de autenticação"
  ↓
  ├─→ Manus (orquestrador): Define prioridades e gerencia o processo
  │
  ├─→ Cursor (executor): Implementa o código de validação
  │
  └─→ Lingma (especialista): Analisa vulnerabilidades e propõe soluções avançadas
  ↓
  Respostas consolidadas e visíveis para toda a Equipe
```

## Implementação Técnica

O protocolo SLI suporta a comunicação tridirecional através do parâmetro de destino "equipe":

```bash
./.manus/scripts/communication.sh enviar "[origem]" "equipe" "[tipo_mensagem]" "Equipe: [conteúdo]" "[requer_resposta]"
```

Exemplo:

```bash
./.manus/scripts/communication.sh enviar "manus" "equipe" "comando" "Equipe: revisar todo o código de autenticação para validar segurança" "true"
```

## Papéis na Comunicação Tridirecional

Cada membro da Equipe tem responsabilidades específicas:

### Manus (Orquestrador)

- Recebe e processa a instrução principal
- Coordena o trabalho entre Cursor e Lingma
- Estabelece prioridades e prazos
- Consolida resultados

### Cursor (Executor de Código)

- Concentra-se em implementar o código
- Testa a funcionalidade
- Documenta implementações
- Reporta bugs e problemas técnicos

### Lingma (Especialista Avançado)

- Oferece análises aprofundadas
- Propõe otimizações e soluções avançadas
- Identifica vulnerabilidades
- Sugere arquiteturas escaláveis

## Exemplos de Uso

### Exemplo 1: Revisar Performance de Sistema

```bash
# Administrador ou Manus inicia a comunicação
./.manus/scripts/communication.sh enviar "manus" "equipe" "comando" "Equipe: analisar e otimizar performance do módulo de relatórios" "true"

# Cada agente processa de acordo com sua especialidade
# Manus responde com plano de orquestração
./.manus/scripts/communication.sh enviar "manus" "equipe" "resposta" "Coordenando análise com foco em: 1) Identificar gargalos, 2) Implementar melhorias, 3) Testar resultados" "false"

# Cursor responde com foco em implementação
./.manus/scripts/communication.sh enviar "cursor" "equipe" "resposta" "Identificando ineficiências no código atual e preparando implementação de melhorias" "false"

# Lingma responde com análise avançada
./.manus/scripts/communication.sh enviar "lingma" "equipe" "resposta" "Analisando complexidade algorítmica e propondo otimizações: 1) Índices na consulta MongoDB, 2) Implementação de cache, 3) Paralelização de processamento" "false"
```

### Exemplo 2: Resolver Bug Crítico

```bash
# Administrador inicia comunicação tridirecional
"Equipe: corrigir urgentemente o bug na autenticação que permite bypass de segurança"

# Resposta coordenada pelos três agentes, cada um em sua especialidade
```

## Boas Práticas

1. **Use o prefixo "Equipe:"** sempre que a tarefa beneficiar-se de perspectivas múltiplas
2. **Aproveite as especialidades** únicas de cada agente
3. **Evite sobrecarga** - use comunicação tridirecional apenas quando necessário
4. **Mantenha o contexto** para que todos os agentes entendam o problema
5. **Especifique prazos** quando relevante
6. **Indique prioridades** para alinhar o foco da Equipe

## Conclusão

A comunicação tridirecional com o prefixo "Equipe:" permite aproveitar ao máximo as capacidades combinadas de Manus, Cursor e Lingma. Use este protocolo quando a tarefa exigir orquestração, implementação e otimização avançada simultaneamente.
