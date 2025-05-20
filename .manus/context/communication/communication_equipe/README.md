# Comunicação da Equipe

Este diretório contém documentação e arquivos relacionados à comunicação entre todos os membros da Equipe no projeto Sistema de Fechamento de Caixa.

## Arquivos Importantes

- **`comunicacao_tridirecional.md`** - Protocolo oficial para comunicação entre todos os agentes (Manus, Cursor, Lingma) simultaneamente
- **`communication.json`** - Registro estruturado das comunicações da Equipe
- **`comunicacao_equipes.md`** - Diretrizes gerais para comunicação da Equipe

## Protocolo Oficial de Comunicação SLI

O único protocolo oficialmente aprovado para a comunicação entre membros da Equipe é o SLI (Shell Line Interface). Este protocolo está documentado em detalhes no diretório principal de comunicação.

### Comunicação Tridirecional com "Equipe:"

Quando uma mensagem começar com **`Equipe:`**, tanto no terminal quanto em qualquer comunicação do Administrador ou Manus, a comunicação deve ser automaticamente tridirecional:

1. A mensagem deve ser distribuída para todos os agentes (Manus, Cursor e Lingma)
2. Todos os agentes devem processar a mensagem e responder conforme suas capacidades
3. As respostas devem ser visíveis para todos os outros agentes

Implementação:

```bash
# Envio de mensagem tridirecional via SLI
./.manus/scripts/communication.sh enviar "[origem]" "equipe" "tipo_mensagem" "Equipe: [conteúdo]" "requer_resposta"
```

## Boas Práticas para Comunicação em Equipe

1. **Use o protocolo SLI exclusivamente** para garantir comunicação confiável
2. **Utilize o prefixo "Equipe:"** quando precisar de participação simultânea de todos os agentes
3. **Mantenha o contexto claro** para que todos os agentes entendam o objetivo
4. **Aproveite as especialidades** de cada agente ao distribuir tarefas
5. **Documente decisões importantes** para referência futura

## Exemplos de Uso

### Comunicação Tridirecional

```bash
# Manus inicia comunicação para toda a Equipe
./.manus/scripts/communication.sh enviar "manus" "equipe" "comando" "Equipe: revisar arquitetura do módulo de autenticação" "true"
```

### Verificando Respostas

```bash
# Verificando respostas para cada agente
./.manus/scripts/communication.sh ler "manus"
./.manus/scripts/communication.sh ler "cursor"
./.manus/scripts/communication.sh ler "lingma"
```

## Integração com Dialog.txt

Mensagens do tipo `equipe` são automaticamente registradas no arquivo `dialog.txt` na raiz do projeto, mantendo compatibilidade com processos existentes.

## Considerações Finais

A comunicação eficiente entre todos os membros da Equipe é crucial para o sucesso do projeto. Utilize o protocolo SLI e a comunicação tridirecional "Equipe:" para maximizar a colaboração e aproveitamento das habilidades complementares de cada agente.
