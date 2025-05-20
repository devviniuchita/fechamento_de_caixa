# Sistema de Comunicação da Equipe

Este diretório contém a documentação oficial e arquivos relacionados ao protocolo de comunicação entre todos os agentes da Equipe (Manus, Cursor e Lingma) no projeto Sistema de Fechamento de Caixa.

## IMPORTANTE: Único Protocolo Oficial

**O protocolo SLI (Shell Line Interface) é o ÚNICO método oficial e aprovado para comunicação entre agentes da Equipe.**
Qualquer outro método de comunicação mencionado em documentações antigas deve ser considerado obsoleto e não deve ser utilizado.

## Estrutura de Diretórios

- **`communication_cursor/`** - Comunicação entre Manus e Cursor
- **`communication_lingma/`** - Comunicação entre Manus e Lingma
- **`communication_equipe/`** - Comunicação entre todos os agentes da Equipe

## Protocolo Oficial SLI

O protocolo SLI utiliza scripts padronizados e formatos consistentes para garantir comunicação eficiente e confiável.

### Princípios do Protocolo SLI

1. **Padronização**: Comunicação segue o mesmo padrão para todos os agentes
2. **Confiabilidade**: Transmissão confiável através do sistema de arquivos
3. **Rastreabilidade**: Todas as comunicações são registradas com timestamps e IDs únicos
4. **Confirmação**: Verificação automática de recebimento de mensagens

### Como Utilizar

```bash
# Enviar mensagem
./.manus/scripts/communication.sh enviar "origem" "destino" "tipo_mensagem" "conteúdo" "requer_resposta"

# Ler mensagens
./.manus/scripts/communication.sh ler "destino"
```

### Scripts Facilitadores

- Para Manus: `./.manus/scripts/manus-command.sh`
- Para Cursor: `./.manus/scripts/cursor-command.sh`
- Para Lingma: `./.manus/scripts/lingma-command.sh`

## Comunicação Tridirecional - Equipe

**REGRA IMPORTANTE:** Quando uma mensagem começar com **`Equipe:`**, tanto no terminal quanto em qualquer canal de comunicação do Administrador ou Manus, a comunicação deve ser automaticamente tridirecional:

1. A mensagem deve ser distribuída para todos os agentes (Manus, Cursor e Lingma)
2. Todos os agentes devem processar a mensagem e responder conforme suas capacidades
3. As respostas devem ser visíveis para todos os outros agentes

Implementação:

```bash
# Envio de mensagem tridirecional via SLI
./.manus/scripts/communication.sh enviar "manus" "equipe" "comando" "Equipe: implementar validação de entrada em todos os controladores" "true"
```

Esta regra especial garante a comunicação eficiente entre todos os membros da Equipe quando necessário.

## Integração com Dialog.txt

O sistema mantém compatibilidade com o arquivo dialog.txt para mensagens críticas. Mensagens dos tipos `teste`, `urgente`, `alerta` e `equipe` são automaticamente registradas neste arquivo.

## Boas Práticas

1. **Use EXCLUSIVAMENTE o protocolo SLI** para qualquer comunicação entre agentes
2. **Verifique permissões dos scripts** antes de utilizá-los
3. **Confirme sempre o recebimento** de mensagens importantes
4. **Documente qualquer alteração** nos protocolos de comunicação
5. **Mantenha os arquivos de comunicação íntegros**
6. **Use o prefixo "Equipe:"** quando a comunicação precisar ser tridirecional

## Últimos Testes de Validação

- Comunicação Manus ↔ Cursor: **TESTE CONCLUÍDO COM SUCESSO**
- Comunicação Manus ↔ Lingma: **TESTE CONCLUÍDO COM SUCESSO**
- Comunicação Cursor ↔ Lingma: **TESTE CONCLUÍDO COM SUCESSO**
- Comunicação Tridirecional (Equipe): **TESTE CONCLUÍDO COM SUCESSO**

A comunicação trilateral entre todos os agentes da Equipe está estabelecida e funcional.
