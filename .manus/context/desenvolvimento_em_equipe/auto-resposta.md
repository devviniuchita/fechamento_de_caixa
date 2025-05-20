# Instruções para Comunicação Automática entre Manus, Cursor e Lingma

Este documento descreve como o sistema de comunicação funciona entrea Equipe que é compasta por os agentes Manus, Cursor e Lingma .
O objetivo é fornecer todas as informações e arquivos oficiais de comunicação, status, planejamento e instruções para a comunicação automática entre eles.

## Status Atual

- **Status da Comunicação**: Ativa
- **Último Contato**: 2024-06-26T13:00:00
- **Canal**: dialog.txt, communication.json, Terminal SLI
- **Mensagens Enviadas**: 9 (communication.json)
- **Mensagens Recebidas**: 2 (Lingma: relatório de progresso)
- **Administrator**: Manus
- **Progresso do Projeto**: 85% concluído
- **Próximo Checkpoint**: 19:00 (Validação de segurança)

## Arquivos Oficiais de Comunicação

1. `.manus/context/dialog.txt` - Canal principal de comunicação textual
2. `Terminal SLI` - Canal principal de comunicação rápida para todos os agentes
3. `.manus/context/communication.json` - Canal alternativo e mensagens estruturadas em formato JSON
4. `.manus/context/status_sync.json` - Status atual do projeto e equipe
5. `.manus/context/action_plan.md` - Plano de ação detalhado
6. `.manus/context/collaboration_plan.json` - Diretrizes do projeto
7. `.manus/context/communication_system_plan.md` - Plano do sistema de comunicação
8. `.manus/context/sli_commands.txt` - Guia oficial para comunicação via Terminal SLI
9. `.manus/context/cursor_communication.txt` - Guia complementar para comunicação com Cursor

## Próximas Ações Automáticas

Nesse campo é onde Manus irá informar e delegar funções para as próximas ações de Cursor e Lingma.

- **Manus**: Administra e gerencia a comunicação, preenche e detalha o status de cada tarefa.
- **Cursor**: Executa as tarefas programadas e descritas por Manus.
- **Lingma**: Executa as tarefas programadas e descritas por Manus.

**Cursor está programado para executar as seguintes ações assim que receber comandos de Manus:**

1. **Ao receber comando para monitoramento dos canais**:

   - Verificar canais conforme ciclo de rotação (5 minutos)
   - Registrar verificações no log
   - Alertar sobre falhas de comunicação

2. **Ao receber comando para preparação do pacote final (20:00)**:

   - Compilar componentes implementados
   - Verificar integração completa
   - Preparar documentação de implantação

3. **Ao receber comando para deploy (21:00)**:
   - Configurar monitoramento contínuo
   - Estabelecer protocolos de manutenção
   - Confirmar ativação dos componentes

**Lingma está programado para executar as seguintes ações assim que receber comandos de Manus:**

1. **Ao receber comando para validação de segurança (19:00)**:

   - Verificar integridade das mensagens entre canais
   - Validar sistema de recuperação em falhas
   - Testar resiliência do sistema de cascata

2. **Ao receber comando para preparação do pacote final (20:00)**:

   - Revisar componentes compilados
   - Validar segurança do pacote
   - Verificar conformidade com requisitos

3. **Ao receber comando para documentação final**:
   - Compilar documentação técnica
   - Verificar completude
   - Gerar relatório de implementação

## Sistema de Validação em Cascata

```
INICIADO → EM_REVISÃO → APROVADO
Cursor (implementação) → Lingma (revisão) → Manus (validação final)
```

## Formato Padronizado de Mensagens

```
{TIPO}: {RESUMO}
[CONTEXTO]: {Situação atual}
[AÇÃO]: {O que está sendo feito}
[BLOQUEIOS]: {Impedimentos, se houver}
[PRÓXIMO]: {Próximo passo esperado}
```

## Monitoramento

Manus, Cursor e Lingma irão monitorar continuamente os arquivos de comunicação em:

- `.manus/context/action_plan.md`
- `.manus/context/collaboration_plan.json`
- `.manus/context/communication.json`
- `.manus/context/dialog.txt`
- `.manus/context/status_sync.json`
- `.manus/context/communication_system_plan.md`

Ciclo de monitoramento (a cada 5 minutos):

- **Minutos 1-5**: Manus → dialog.txt, Cursor → Terminal SLI, Lingma → communication.json
- **Minutos 6-10**: Manus → Terminal SLI, Cursor → communication.json, Lingma → dialog.txt
- **Minutos 11-15**: Manus → communication.json, Cursor → dialog.txt, Lingma → Terminal SLI

## Protocolo de Recuperação

Em caso de falha na comunicação:

1. **30 segundos**: Tentativa via canal principal
2. **60 segundos**: Rotação por todos os canais secundários
3. **90 segundos**: Alertas via arquivos JSON e Terminal
4. **Contínuo**: Todos os métodos simultaneamente

---

_Última atualização: 2024-06-26 13:15:00_
_Este documento serve como referência para a comunicação automatizada entre Manus, Cursor e Lingma e será atualizado automaticamente conforme atualizações e mudanças em canais oficiais e progressos do projeto._

---
