# Módulo de Fechamento de Caixa

## Visão Geral

Responsável por gerenciar a rotina de abertura, fechamento e conferência de caixas.

## Entidades

- **FechamentoCaixa**: id, data, usuario, valorInicial, entradas, saidas, valorFinal, observacoes, status

## Regras de Negócio

- Calcular valor final automaticamente
- Validar consistência dos valores
- Restringir ações por perfil (ADMIN, GERENTE, CAIXA)
- Permitir conferência apenas após fechamento

## Endpoints Esperados

- `GET /fechamentos` — listar todos
- `GET /fechamentos/{id}` — buscar por ID
- `POST /fechamentos` — criar fechamento
- `PUT /fechamentos/{id}` — atualizar
- `PATCH /fechamentos/{id}/fechar` — fechar caixa
- `PATCH /fechamentos/{id}/conferir` — conferir fechamento
- `DELETE /fechamentos/{id}` — excluir fechamento
