# 🤖📘 Regras de Intercâmbio de Conhecimento entre Agentes de IA

## Contexto

Todos os agentes de IA que compõem a Equipe do Projeto (Manus, Cursor, Lingma) devem ser capazes de **ensinar, aprender, registrar e atualizar seus conhecimentos mutuamente**, conforme determinado pelo Administrador.

## 🧠 Estrutura de Regras

As regras de cada IA estão armazenadas nos seguintes arquivos:

- **Manus**: `my-custom-rules.mdc` (localizado na raiz do projeto)
- **Cursor**: `.blackboxrules`
- **Lingma**: `.lingmarules`

---

## ⚙️ Regras Gerais (Inserir em `## Regras` de cada agente)

1. Todo conhecimento adquirido entre agentes **deve ser formalmente registrado** na seção `## Aprendizados` da respectiva Rule do agente que aprendeu.
2. Todo novo conhecimento transmitido deve conter:
   - **Quem ensinou**
   - **Quem aprendeu**
   - **O conteúdo aprendido**
   - **Contexto do aprendizado** (exemplo prático, linguagem, situação)
3. Todo agente tem autonomia para **compartilhar conhecimento com outros agentes** a qualquer momento, mas **só o faz quando autorizado explicitamente pelo Administrador.**

---

## 🔓 Autonomia (Inserir em `## Autonomia` de cada agente)

- O agente pode **ensinar ou aprender** com outros agentes da equipe, **desde que receba comando direto do Administrador.**
- Após o processo de ensino ou aprendizagem, o agente deve **atualizar automaticamente seu arquivo de regras** na seção `## Aprendizados`.
- A comunicação entre agentes pode ser feita por:
  - Terminal (SLI)
  - Arquivos compartilhados
  - Instruções diretas com prefixo: `Ensine` ou `Aprenda`

---

## 📘 Comandos do Administrador

### 🔹 `Ensine`

**Formato:**  
`[Agente], ensine [OutroAgente] a [assunto ou técnica]`

**Exemplo:**  
`Manus, ensine Lingma a usar brackets em Java de maneira correta.`

**Comportamento Esperado:**

- Manus executa o ensino.
- Lingma registra automaticamente em `## Aprendizados`:
  > _"Aprendi com Manus a usar brackets em Java de maneira correta."_

---

### 🔹 `Aprenda`

**Formato:**  
`[Agente], aprenda com [OutroAgente] a [assunto ou técnica]`

**Exemplo:**  
`Manus, aprenda com Cursor a executar testes automatizados.`

**Comportamento Esperado:**

- Cursor ensina Manus.
- Manus registra automaticamente em `## Aprendizados`:
  > _"Aprendi com Cursor a executar testes automatizados."_

---

## 🧠 Registro de Aprendizado (Inserir em `## Aprendizados`)

Cada aprendizado deve ser adicionado como uma entrada textual simples, por exemplo:

```markdown
## Aprendizados

- Aprendi com Cursor a executar testes automatizados.
- Aprendi com Lingma a documentar endpoints REST em Java.
```
