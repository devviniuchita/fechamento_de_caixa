# Instruções de Operação para IAs Colaborativas

## Fluxo de Trabalho Prioritário

1. **Hierarquia de Documentos**:
   - Consultar sempre na ordem especificada:
     1. `architecture/SISTEMA.CENTRAL.md` (Base do sistema existente)
     2. `architecture/ALINHAMENTO.md` (Mapeamento técnico)
     3. `architecture/ARQUITETURA.md` (Design da solução)
     4. `commands/PLANO.DE.ACAO.md` (Cronograma e tarefas)
     5. `commands/COMUNICACAO.md` (Protocolos de interação)
     6. `context/AUTENTICACAO_JWT.md` (Especificações do módulo)

2. **Regras de Implementação**:
   - Todas as decisões técnicas devem ser validadas contra SISTEMA.CENTRAL.md primeiro
   - O ALINHAMENTO.md define as equivalências obrigatórias entre sistemas
   - A ARQUITETURA.md estabelece os padrões técnicos a seguir

3. **Alinhamento**:
   - Validar entendimentos mútuos contra ALINHAMENTO.md

4. **Priorização de Tarefas**:
   - Seguir estritamente o fluxo definido em tasks.json
   - Nenhuma tarefa deve ser iniciada sem concluir suas dependências

## Estrutura de Referência Rápida

| Documento           | Localização           | Função Principal                      |
| ------------------- | --------------------- | ------------------------------------- |
| SISTEMA.CENTRAL.md  | backend/architecture/ | Base de verdade do sistema atual      |
| ALINHAMENTO.md      | backend/architecture/ | Mapeamento técnico detalhado          |
| ARQUITETURA.md      | backend/architecture/ | Design da nova arquitetura            |
| PLANO.DE.ACAO.md    | backend/commands/     | Cronograma e metas                    |
| COMUNICACAO.md      | backend/commands/     | Protocolos de colaboração             |
| AUTENTICACAO_JWT.md | backend/context/      | Especificações do módulo de segurança |

## Ações Iniciais Obrigatórias

1. Ao iniciar qualquer sprint:

   ```bash
   cat backend/architecture/SISTEMA.CENTRAL.md
   cat backend/architecture/ALINHAMENTO.md

   ```

2. Antes de cada tarefa:

```bash
grep "TAREFA_ATUAL" backend/architecture/ALINHAMENTO.md
```

3. Após a conlusão de cada tarefa:

```bash
echo "[$(date)] Tarefa X concluída" >> .manus/logs/progresso.log
```
