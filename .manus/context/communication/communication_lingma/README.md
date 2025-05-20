# Comunicação Oficial Manus-Lingma

Este diretório contém a documentação oficial sobre o método único de comunicação instantânea entre os agentes Manus e Lingma no projeto Sistema de Fechamento de Caixa.

## Arquivo Principal

[**comunicacao_oficial.md**](./comunicacao_oficial.md) - **DOCUMENTO ÚNICO E OFICIAL** que define o protocolo de comunicação SLI instantânea entre Manus e Lingma. Este é o único método aprovado para comunicação entre os agentes.

## Método Oficial de Comunicação Instantânea

O único método de comunicação aprovado é:

**1. Para enviar mensagem para Lingma:**

```bash
./.manus/scripts/communication.sh enviar "manus" "lingma" "tipo_mensagem" "conteúdo" "true/false"
```

**2. Para verificar respostas imediatamente:**

```bash
./.manus/scripts/communication.sh ler "manus"
```

Este método proporciona comunicação instantânea bidirecional validada em todos os testes.

## Arquivos de Suporte

- [**teste_comunicacao.log**](./teste_comunicacao.log) - Registro dos testes de comunicação instantânea realizados.

## Importante

Qualquer outro método de comunicação entre Manus e Lingma não documentado em `comunicacao_oficial.md` deve ser desconsiderado. O método oficial substituiu todos os métodos anteriores e é o único aprovado para uso.

Última atualização: 19 de maio de 2025
