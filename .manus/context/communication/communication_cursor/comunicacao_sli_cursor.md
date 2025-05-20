# Protocolo Oficial de Comunicação SLI: Manus ↔ Cursor

## Introdução

Este documento define o protocolo oficial SLI (Shell Line Interface) para comunicação bilateral entre Manus (orquestrador) e Cursor (executor de código) no projeto Sistema de Fechamento de Caixa. **Este é o único protocolo oficial aprovado para comunicação entre agentes da Equipe.**

## Princípios do Protocolo SLI

1. **Padronização**: A comunicação segue o mesmo padrão para todos os agentes da Equipe
2. **Confiabilidade**: Transmissão confiável de instruções através do sistema de arquivos
3. **Rastreabilidade**: Todas as comunicações são registradas com timestamps e IDs únicos
4. **Confirmação**: Verificação automática de recebimento de mensagens

## Protocolo Detalhado SLI

### 1. Enviando Mensagens de Manus para Cursor

```bash
# Formato padronizado SLI
./.manus/scripts/communication.sh enviar "manus" "cursor" "tipo_mensagem" "conteúdo" "requer_resposta"
```

Exemplo:

```bash
./.manus/scripts/communication.sh enviar "manus" "cursor" "comando" "Implemente o modelo FechamentoCaixa.java com os atributos id, data e valor." "true"
```

### 2. Manus verificando respostas do Cursor

```bash
./.manus/scripts/communication.sh ler "manus"
```

### 3. Cursor enviando mensagens para Manus

```bash
./.manus/scripts/communication.sh enviar "cursor" "manus" "tipo_mensagem" "conteúdo" "requer_resposta"
```

Exemplo:

```bash
./.manus/scripts/communication.sh enviar "cursor" "manus" "resposta" "Modelo FechamentoCaixa.java implementado com sucesso." "false"
```

### 4. Cursor verificando mensagens de Manus

```bash
./.manus/scripts/communication.sh ler "cursor"
```

## Scripts Facilitadores

O projeto inclui scripts facilitadores para simplificar a comunicação SLI:

### Para Manus

```bash
# Enviar comando para Cursor
./.manus/scripts/manus-command.sh enviar "tipo_mensagem" "conteúdo" "requer_resposta"

# Ler respostas do Cursor
./.manus/scripts/manus-command.sh ler

# Verificar status do sistema
./.manus/scripts/manus-command.sh status
```

### Para Cursor

```bash
# Enviar resposta para Manus
./.manus/scripts/cursor-command.sh responder "tipo_mensagem" "conteúdo" "requer_resposta"

# Ler comandos de Manus
./.manus/scripts/cursor-command.sh ler

# Enviar status para Manus
./.manus/scripts/cursor-command.sh status
```

## Tipos de Mensagens Padronizadas

| Tipo       | Descrição                         | Exemplo                                     |
| ---------- | --------------------------------- | ------------------------------------------- |
| `comando`  | Instrução para implementar código | Implemente o modelo FechamentoCaixa.java    |
| `teste`    | Verificar comunicação             | Cursor, está me ouvindo? Responda.          |
| `status`   | Informar estado atual             | Implementação 70% concluída                 |
| `resposta` | Responder a uma solicitação       | Modelo implementado com sucesso             |
| `erro`     | Reportar problema                 | Não foi possível compilar o código          |
| `urgente`  | Alta prioridade                   | Corrija imediatamente o bug crítico         |
| `aviso`    | Informação importante             | Detectado potencial problema de performance |
| `query`    | Solicitação de informação         | Qual é o status da implementação atual?     |
| `equipe`   | Comunicação tridirecional         | Solicitação para todos os agentes           |

## Comunicação Tridirecional - Equipe

**REGRA IMPORTANTE:** Quando uma mensagem começar com **`Equipe:`**, tanto no terminal quanto em qualquer comunicação do Administrador ou Manus, a comunicação deve ser automaticamente tridirecional, ou seja:

1. A mensagem deve ser distribuída para todos os agentes (Manus, Cursor e Lingma)
2. Todos os agentes devem processar a mensagem e responder conforme suas capacidades
3. As respostas devem ser visíveis para todos os outros agentes

Implementação:

```bash
# Envio de mensagem tridirecional via SLI
./.manus/scripts/communication.sh enviar "manus" "equipe" "comando" "Equipe: implementar validação de entrada em todos os controladores" "true"
```

Esta regra especial garante a comunicação eficiente entre todos os membros da Equipe quando necessário.

## Exemplos Completos de Comunicação SLI

### Exemplo 1: Solicitação de Implementação

```bash
# Manus envia comando para Cursor
./.manus/scripts/communication.sh enviar "manus" "cursor" "comando" "Implementar método validarFechamento() na classe FechamentoCaixa.java" "true"

# Cursor verifica mensagens pendentes
./.manus/scripts/communication.sh ler "cursor"

# Cursor responde confirmando recebimento
./.manus/scripts/communication.sh enviar "cursor" "manus" "status" "Iniciando implementação do método validarFechamento()" "false"

# Cursor informa conclusão
./.manus/scripts/communication.sh enviar "cursor" "manus" "resposta" "Método validarFechamento() implementado com sucesso" "false"

# Manus verifica a resposta
./.manus/scripts/communication.sh ler "manus"
```

### Exemplo 2: Teste de Comunicação

```bash
# Manus testa comunicação
./.manus/scripts/communication.sh enviar "manus" "cursor" "teste" "Cursor, verificando canal de comunicação SLI. Por favor confirme." "true"

# Cursor verifica mensagens
./.manus/scripts/communication.sh ler "cursor"

# Cursor responde ao teste
./.manus/scripts/communication.sh enviar "cursor" "manus" "teste" "Comunicação SLI funcionando normalmente" "false"

# Manus verifica a resposta
./.manus/scripts/communication.sh ler "manus"
```

### Exemplo 3: Comunicação Tridirecional (Equipe)

```bash
# Manus envia mensagem para toda a Equipe
./.manus/scripts/communication.sh enviar "manus" "equipe" "comando" "Equipe: iniciar revisão de código do módulo de autenticação" "true"

# Cursor e Lingma verificam mensagens
./.manus/scripts/communication.sh ler "cursor"
./.manus/scripts/communication.sh ler "lingma"

# Cada agente responde conforme sua especialidade
./.manus/scripts/communication.sh enviar "cursor" "equipe" "resposta" "Iniciando revisão da implementação do código de autenticação" "false"
./.manus/scripts/communication.sh enviar "lingma" "equipe" "resposta" "Analisando otimizações e segurança do módulo de autenticação" "false"

# Todos verificam as respostas
./.manus/scripts/communication.sh ler "manus"
./.manus/scripts/communication.sh ler "cursor"
./.manus/scripts/communication.sh ler "lingma"
```

## Protocolo de Recuperação SLI

Se a comunicação for perdida:

1. **Verificar arquivo de comunicação**:

   ```bash
   cat .manus/context/communication.json
   ```

2. **Enviar mensagem de teste**:

   ```bash
   ./.manus/scripts/communication.sh enviar "manus" "cursor" "teste" "Verificando recuperação do canal SLI. Por favor confirme." "true"
   ```

3. **Aguardar e verificar resposta**:
   ```bash
   ./.manus/scripts/communication.sh ler "manus"
   ```

## Boas Práticas SLI

1. **Verifique permissões dos scripts**: Garanta que todos os scripts tenham permissões de execução (`chmod +x`)
2. **Valide o formato completo**: Sempre inclua todos os parâmetros necessários
3. **Verifique respostas regularmente**: Estabeleça um ritmo de verificação de mensagens
4. **Use IDs específicos**: Para comunicações importantes, use referências específicas para contexto
5. **Mantenha o arquivo de comunicação íntegro**: Evite editar manualmente o arquivo `communication.json`
6. **Use o prefixo "Equipe:"**: Quando a comunicação precisar ser tridirecional

## Integração com Dialog.txt

Para manter compatibilidade com processos existentes, mensagens críticas (tipos: `teste`, `urgente`, `alerta`, `equipe`) são automaticamente registradas no arquivo `dialog.txt`, seguindo o formato:

```
[ORIGEM] TIPO: Conteúdo
```

## Conclusão

Este protocolo SLI padroniza a comunicação entre todos os agentes da Equipe, garantindo consistência, confiabilidade e rastreabilidade em todas as interações. A comunicação tridirecional com o prefixo "Equipe:" permite coordenação eficiente quando necessário.
