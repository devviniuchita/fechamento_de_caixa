# Protocolo Oficial de Comunicação SLI: Manus ↔ Lingma

Este documento define o único protocolo oficial para comunicação entre Manus e Lingma no projeto Sistema de Fechamento de Caixa. **Qualquer outro método de comunicação diferente do descrito aqui deve ser ignorado.**

## O que é SLI?

SLI (Shell Line Interface) é o canal oficial de comunicação entre Manus e Lingma. Ele utiliza comandos de terminal para enviar mensagens estruturadas entre os agentes de forma instantânea.

## MÉTODO OFICIAL DE COMUNICAÇÃO (INSTANTÂNEO)

### 1. Comunicação Manus → Lingma

Use exclusivamente o script communication.sh:

```bash
# Enviar mensagem para Lingma
./.manus/scripts/communication.sh enviar "manus" "lingma" "tipo_mensagem" "conteúdo" "requer_resposta"
```

Exemplo:

```bash
./.manus/scripts/communication.sh enviar "manus" "lingma" "comando" "Gere um relatório de desempenho" "true"
```

### 2. Verificar Respostas de Lingma

Use exclusivamente:

```bash
# Verificar respostas
./.manus/scripts/communication.sh ler "manus"
```

### 3. Interface Simplificada para Comandos Específicos (OPCIONAL)

Para comandos rápidos e específicos, pode-se utilizar a interface simplificada:

```bash
# Verificar status do Lingma
./.manus/scripts/lingma-command.sh status
```

### 4. Receber Mensagens de Lingma (Comunicação Bidirecional)

As mensagens de Lingma são recebidas instantaneamente através do communication.sh:

```bash
# Mensagem enviada por Lingma
./.manus/scripts/lingma-command.sh responder "tipo_mensagem" "conteúdo" "requer_resposta"
```

## Tipos de Mensagens Oficiais

- **comando**: Instruções diretas
- **teste**: Verificação de comunicação
- **status**: Atualizações de estado
- **resposta**: Confirmações
- **urgente**: Alta prioridade

## Procedimento Oficial Recomendado

1. **Para enviar mensagem para Lingma**:

   ```bash
   ./.manus/scripts/communication.sh enviar "manus" "lingma" "tipo_mensagem" "conteúdo" "true/false"
   ```

2. **Para verificar respostas**:
   ```bash
   ./.manus/scripts/communication.sh ler "manus"
   ```

## Demonstração de Comunicação Instantânea

1. **Envio da mensagem**:

   ```bash
   ./.manus/scripts/communication.sh enviar "manus" "lingma" "teste" "Teste de comunicação instantânea" "true"
   ```

2. **Verificação imediata da resposta**:
   ```bash
   ./.manus/scripts/communication.sh ler "manus"
   ```
   Resultado: Resposta recebida instantaneamente.

## Nota Importante

Este é o único protocolo oficial de comunicação entre Manus e Lingma. Qualquer referência a outros métodos em documentos antigos deve ser desconsiderada. O método documentado aqui foi validado como instantâneo e confiável.

Todos os testes confirmam que este método proporciona comunicação bidirecional instantânea entre os agentes.

Última atualização: 19 de maio de 2025
