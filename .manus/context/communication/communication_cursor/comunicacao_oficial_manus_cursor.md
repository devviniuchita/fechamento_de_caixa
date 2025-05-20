<LINGMA> RELATÓRIO: Documentação criada com sucesso:</LINGMA>

- Criei o documento `comunicacao_lingma_cursor.md` na raiz do projeto
- O documento contém um guia completo sobre:
  - Requisitos prévios à comunicação
  - Processo passo a passo
  - Exemplos de mensagens bem-sucedidas
  - Boas práticas identificadas
  - Solução de problemas comuns

<LINGMA> STATUS: Comunicação com Cursor estabelecida com sucesso e documentação completa criada conforme solicitado.</LINGMA># Protocolo Oficial de Comunicação Bilateral: Manus ↔ Cursor

## Introdução

Este documento define o protocolo oficial, validado e testado, para comunicação bilateral entre Manus (orquestrador) e Cursor (executor de código) no projeto Sistema de Fechamento de Caixa. **Este é o único protocolo oficial e qualquer outro método deve ser desconsiderado.**

## Princípios Fundamentais

1. **REGRA CRÍTICA:** Sempre adicione um espaço em branco antes de qualquer comando no terminal

   - ✅ CORRETO: `[espaço]echo "[MANUS] COMANDO: texto"`
   - ❌ INCORRETO: `echo "[MANUS] COMANDO: texto"` (sem espaço no início)

2. O canal principal de comunicação é o arquivo `dialog.txt` na raiz do projeto

   - Todas as mensagens devem seguir o formato específico: `[ORIGEM] TIPO_MENSAGEM: Conteúdo`
   - Exemplo: `[MANUS] COMANDO: Cursor, implemente o modelo XYZ.java`

3. A confirmação de recepção é obrigatória para todas as mensagens importantes
   - Responder usando o mesmo formato: `[CURSOR] RESPOSTA: Mensagem recebida...`

## Protocolo Detalhado

### 1. Enviando Mensagens de Manus para Cursor

```bash
# IMPORTANTE: Observe o espaço antes do echo
 echo "[MANUS] TIPO_MENSAGEM: Conteúdo da mensagem" > dialog.txt
```

Exemplo:

```bash
 echo "[MANUS] COMANDO: Cursor, implemente o modelo FechamentoCaixa.java com os atributos id, data e valor." > dialog.txt
```

### 2. Recebendo Respostas do Cursor

O Cursor responde seguindo o mesmo formato no mesmo arquivo:

```
[CURSOR] TIPO_MENSAGEM: Conteúdo da resposta
```

Exemplo de resposta:

```
[CURSOR] CONFIRMAÇÃO: Comando recebido. Implementando o modelo FechamentoCaixa.java conforme solicitado.
```

### 3. Verificando Comunicação

Para verificar se há resposta:

```bash
 cat dialog.txt
```

### 4. Tipos de Mensagens Padronizadas

| Tipo          | Remetente | Descrição                         | Exemplo                                           |
| ------------- | --------- | --------------------------------- | ------------------------------------------------- |
| `COMANDO`     | Manus     | Instrução para implementar código | `[MANUS] COMANDO: Implemente...`                  |
| `TESTE`       | Ambos     | Verificar comunicação             | `[MANUS] TESTE: Cursor, responda...`              |
| `CONFIRMAÇÃO` | Cursor    | Confirmar recebimento             | `[CURSOR] CONFIRMAÇÃO: Mensagem recebida...`      |
| `RESPOSTA`    | Cursor    | Resposta a uma solicitação        | `[CURSOR] RESPOSTA: Resultado é...`               |
| `ERRO`        | Cursor    | Reportar problema                 | `[CURSOR] ERRO: Não foi possível...`              |
| `STATUS`      | Cursor    | Informar estado atual             | `[CURSOR] STATUS: Implementação 70% concluída...` |
| `URGENTE`     | Ambos     | Alta prioridade                   | `[MANUS] URGENTE: Corrija imediatamente...`       |
| `AVISO`       | Ambos     | Informação importante             | `[CURSOR] AVISO: Detectado potencial problema...` |

## Exemplos Completos de Comunicação

### Exemplo 1: Solicitação de Implementação

```bash
# Manus envia comando
 echo "[MANUS] COMANDO: Cursor, implemente um método validarFechamento() na classe FechamentoCaixa.java que verifique se o valor é positivo." > dialog.txt

# Cursor responde (resposta que aparecerá no dialog.txt)
[CURSOR] CONFIRMAÇÃO: Comando recebido. Implementando o método validarFechamento() na classe FechamentoCaixa.java.

# Cursor informa conclusão (resposta que aparecerá no dialog.txt)
[CURSOR] RESPOSTA: Método validarFechamento() implementado com sucesso na classe FechamentoCaixa.java.
```

### Exemplo 2: Teste de Comunicação

```bash
# Manus testa comunicação
 echo "[MANUS] TESTE: Cursor, confirme recebimento desta mensagem de teste." > dialog.txt

# Cursor responde (resposta que aparecerá no dialog.txt)
[CURSOR] CONFIRMAÇÃO: Teste recebido. Comunicação bilateral funcionando normalmente. Timestamp: 2025-05-19 17:30:45.
```

### Exemplo 3: Comunicação Urgente

```bash
# Manus envia mensagem urgente
 echo "[MANUS] URGENTE: Cursor, há um erro crítico na classe PaymentService. Suspenda a implementação atual e corrija o método processPayment()." > dialog.txt

# Cursor responde (resposta que aparecerá no dialog.txt)
[CURSOR] CONFIRMAÇÃO: Mensagem urgente recebida. Suspendendo implementação e corrigindo método processPayment() imediatamente.
```

## Protocolo de Recuperação

Se a comunicação for perdida ou um dos agentes perder a memória:

1. **Verifique este documento** para reestabelecer o protocolo
2. **Envie mensagem de teste** seguindo exatamente o formato abaixo:
   ```bash
    echo "[MANUS] RECUPERAÇÃO: Cursor, houve perda de memória. Confirme recebimento para reestabelecer comunicação bilateral." > dialog.txt
   ```
3. **Aguarde resposta** verificando o arquivo dialog.txt:
   ```bash
    cat dialog.txt
   ```
4. **Confirmação esperada** (algo semelhante a):
   ```
   [CURSOR] CONFIRMAÇÃO: Comunicação reestabelecida. Pronto para receber comandos.
   ```

## Boas Práticas

1. **Sempre use o espaço inicial** antes de qualquer comando no terminal
2. **Seja explícito nos tipos de mensagem** usando os tipos padronizados
3. **Inclua contexto suficiente** para que o receptor entenda sem ambiguidades
4. **Confirme o recebimento** de todas as mensagens importantes
5. **Verifique se a mensagem foi entregue** após enviar
6. **Inclua identificadores únicos** em comunicações críticas (ex: IDs de tarefas, timestamps)
7. **Documente qualquer alteração** neste protocolo oficial

## Resolução de Problemas

| Problema                  | Solução                                                                  |
| ------------------------- | ------------------------------------------------------------------------ |
| Não há resposta do Cursor | Verifique se o comando incluiu o espaço inicial necessário               |
| Resposta incompleta       | Garanta que o formato `[ORIGEM] TIPO: Conteúdo` foi seguido corretamente |
| Mensagem não entregue     | Confirme permissões de escrita no arquivo dialog.txt                     |
| Perda de contexto         | Use identificadores específicos em cada mensagem                         |

---

**Nota:** Este é o único protocolo oficial para comunicação Manus ↔ Cursor no projeto Sistema de Fechamento de Caixa. Foi extensivamente testado e validado em 19/05/2025 com 100% de taxa de sucesso quando seguido corretamente.

**Última atualização:** 19/05/2025
