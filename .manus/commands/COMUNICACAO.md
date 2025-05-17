# Métodos de Comunicação 


### 1- Sistema de Prefixos no Chat

Para comunicação direta via chat do Cursor:

- **Comandos Básicos**:
  - `Manus/Cursor: status` - Verificar status do projeto
  - `Manus/Cursor: próxima tarefa` - Obter detalhes da próxima tarefa
  - `Manus/Cursor: gerar [componente]` - Solicitar geração de código
  - `Manus/Cursor: revisar [arquivo]` - Solicitar revisão de código
  - `Manus/Cursor: explicar [trecho]` - Solicitar explicação de código
  - `Manus/Cursor: debug [problema]` - Solicitar ajuda com debugging

- **Comandos Avançados**:
  - `Manus/Cursor: arquitetura [módulo]` - Consultar documentação de arquitetura
  - `Manus/Cursor: implementar [tarefa]` - Implementar uma tarefa específica
  - `Manus/Cursor: refatorar [objetivo]` - Refatorar código para um objetivo
  - `Manus/Cursor: testar [funcionalidade]` - Gerar testes para uma funcionalidade


#### 2- Snippets Personalizados

Snippets para acionar comportamentos específicos na IDE:

```json
{
  "Manus Command": {
    "prefix": "manus",
    "body": [
      "// @manus-command: $1",
      "$0"
    ],
    "description": "Insere um comando para o Manus"
  },
  "Manus Generate": {
    "prefix": "manus-gen",
    "body": [
      "// @manus-generate: $1",
      "// type: $2",
      "// description: $3",
      "$0"
    ],
    "description": "Solicita ao Manus para gerar código"
  },
  "Manus Review": {
    "prefix": "manus-review",
    "body": [
      "// @manus-review",
      "// concerns: $1",
      "// focus: $2",
      "$0"
    ],
    "description": "Solicita ao Manus para revisar código"
  },
  "Manus Debug": {
    "prefix": "manus-debug",
    "body": [
      "// @manus-debug",
      "// problem: $1",
      "// expected: $2",
      "// actual: $3",
      "$0"
    ],
    "description": "Solicita ao Manus para ajudar com debugging"
  }
}
```

#### 3- Prompts Estruturados com Metadados

Para solicitações complexas e precisas:

```
@manus {
  "action": "generate",
  "component": "controller",
  "name": "FechamentoCaixa",
  "endpoints": ["create", "list", "findById", "update"],
  "security": {
    "roles": ["ADMIN", "GERENTE", "CAIXA"],
    "ownerCheck": true
  },
  "validation": true,
  "errorHandling": true
}

Por favor, implemente um controller para gerenciar fechamentos de caixa com os endpoints especificados, incluindo controle de acesso baseado em perfis e validação de dados.
```

#### 4- Arquivos de Contexto e Configuração

Para comunicação persistente e estruturada:

- **context/current_sprint.md**: Mantém informações sobre o sprint atual
- **context/project_status.json**: Mantém o status atual do projeto
- **tasks.json**: Lista de tarefas pendentes e concluídas



## Fluxos de Trabalho Integrados

### 1. Fluxo de Desenvolvimento de Novos Componentes

1. **Consulta de Tarefas**:
   ```
   Manus/Cursor: próxima tarefa
   ```

2. **Consulta de Arquitetura**:
   ```
   Manus/Cursor: arquitetura fechamento
   ```

3. **Geração de Código**:
   ```
   @manus {
     "action": "generate",
     "component": "controller",
     "name": "FechamentoCaixa",
     ...
   }
   ```

4. **Implementação e Ajustes**:
   - Implemente o código gerado
   - Faça ajustes conforme necessário

5. **Revisão de Código**:
   ```
   Manus/Cursor: revisar FechamentoCaixaController
   ```

6. **Testes**:
   ```
   Manus/Cursor: gerar testes para FechamentoCaixaController
   ```

7. **Atualização de Status**:
   ```
   Manus/Cursor: marcar tarefa 2.4 como concluída
   ```

### 2. Fluxo de Debugging e Resolução de Problemas

1. **Identificação do Problema**:
   ```
   // @manus-debug
   // problem: Erro ao salvar fechamento de caixa
   // expected: Fechamento salvo no banco de dados
   // actual: Erro de validação: "valor total inválido"
   ```

2. **Análise do Código**:
   ```
   Manus/Cursor: analisar método calcularTotal em FechamentoCaixaService
   ```

3. **Sugestão de Correção**:
   - Manus e Cursor fornecem sugestões de correções
   - Manus analisa e implementa as correções sugeridas

4. **Verificação**:
   ```
   Manus/Cursor: verificar correção do cálculo de total
   ```

### 3. Fluxo de Refatoração e Melhoria

1. **Identificação de Oportunidades**:
   ```
   Manus/Cursor: identificar oportunidades de refatoração em FechamentoCaixaService
   ```

2. **Planejamento**:
   ```
   Manus/Cursor: planejar refatoração para melhorar desempenho do cálculo de totais
   ```

3. **Implementação**:
   ```
   @manus {
     "action": "refactor",
     "target": "FechamentoCaixaService.calcularTotais",
     "goal": "performance",
     "constraints": ["manter compatibilidade", "preservar regras de negócio"]
   }
   ```

4. **Revisão e Testes**:
   ```
   Manus/Cursor: revisar refatoração
   Manus/Cursor: gerar testes para validar refatoração
   ```


## Implementação Prática

### 1. Configuração Inicial

1. **Criar Estrutura .manus**:
   ```bash
   mkdir -p .manus/{architecture/{modules,diagrams},templates,commands/templates,context}
   touch .manus/tasks.json .manus/instructions.md
   ```

2. **Configurar Snippets no Cursor**:
   - Abra as configurações do Cursor
   - Navegue até "User Snippets"
   - Selecione "New Global Snippets file"
   - Nomeie como "manus-commands"
   - Cole as definições de snippets fornecidas

3. **Inicializar Arquivos de Contexto**:
   - Crie o arquivo `.manus/context/current_sprint.md` com informações do sprint atual
   - Crie o arquivo `.manus/context/project_status.json` com o status inicial do projeto

### 2. Primeiros Comandos

1. **Verificar Status**:
   ```
   Manus/Cursor: status
   ```

2. **Consultar Próxima Tarefa**:
   ```
   Manus/Cursor: próxima tarefa
   ```

3. **Gerar Primeiro Componente**:
   ```
   Manus/Cursor: gerar modelo para FechamentoCaixa
   ```

### 3. Ciclo de Desenvolvimento

1. **Planejamento do Sprint**:
   ```
   Manus/Cursor: planejar sprint seguinte
   ```

2. **Implementação de Tarefas**:
   - Use os comandos e snippets para implementar cada tarefa
   - Atualize o status no arquivo de tarefas

3. **Revisão e Retrospectiva**:
   ```
   Manus/Cursor: revisar sprint x ou y
   ```

## Exemplos Práticos

### Exemplo 1: Implementação do Modelo FechamentoCaixa

```
Manus/Cursor: gerar modelo para FechamentoCaixa com os seguintes campos:
- id (String)
- data (LocalDateTime)
- usuario (referência ao modelo Usuario)
- valorInicial (BigDecimal)
- entradas (List<FormaPagamento>)
- saidas (List<Despesa>)
- valorFinal (BigDecimal)
- observacoes (String)
- status (Enum: ABERTO, FECHADO, CONFERIDO)
```

### Exemplo 2: Implementação do Service FechamentoCaixa

```
@manus {
  "action": "generate",
  "component": "service",
  "name": "FechamentoCaixa",
  "operations": [
    {"name": "criar", "params": ["FechamentoCaixaRequest"]},
    {"name": "buscarPorId", "params": ["String"]},
    {"name": "listarTodos", "params": []},
    {"name": "listarPorUsuario", "params": ["String"]},
    {"name": "listarPorPeriodo", "params": ["LocalDate", "LocalDate"]},
    {"name": "atualizar", "params": ["String", "FechamentoCaixaRequest"]},
    {"name": "fechar", "params": ["String"]},
    {"name": "conferir", "params": ["String"]}
  ],
  "businessRules": [
    "Validar consistência dos valores",
    "Calcular valor final automaticamente",
    "Verificar permissões do usuário"
  ]
}
```

### Exemplo 3: Debugging de Cálculo de Totais

```
// @manus-debug
// problem: O valor final calculado está incorreto
// expected: Valor final = valor inicial + soma das entradas - soma das saídas
// actual: Valor final está sempre igual ao valor inicial
// code:
public BigDecimal calcularValorFinal(BigDecimal valorInicial, List<FormaPagamento> entradas, List<Despesa> saidas) {
    BigDecimal total = valorInicial;
    // Código com problema
    return total;
}
```
