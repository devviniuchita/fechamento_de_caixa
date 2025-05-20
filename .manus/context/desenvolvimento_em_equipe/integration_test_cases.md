# Casos de Teste de Integração - MCP Server

## Objetivo

Este documento define os casos de teste de integração para validar a compatibilidade entre o MCP Server original e a versão independente. Os testes garantem que todas as funcionalidades essenciais funcionam de maneira idêntica em ambas as versões.

## Pré-requisitos

1. Ambos os servidores (original e independente) devem estar em execução
2. Ambiente de teste configurado conforme especificado em `performance_test_plan.md`
3. Dados de teste padronizados disponíveis no diretório `/test/resources/test-data`

## Categorias de Teste

### 1. Testes de API Básica

| ID     | Descrição                                    | Endpoint         | Método | Entradas                                    | Saída Esperada                   | Prioridade |
| ------ | -------------------------------------------- | ---------------- | ------ | ------------------------------------------- | -------------------------------- | ---------- |
| API-01 | Verificar status do servidor                 | /api/status      | GET    | Nenhuma                                     | Código 200, JSON com status "UP" | P0         |
| API-02 | Autenticação bem-sucedida                    | /api/auth        | POST   | {"username": "test", "password": "test123"} | Código 200, Token JWT válido     | P0         |
| API-03 | Autenticação com credenciais inválidas       | /api/auth        | POST   | {"username": "test", "password": "invalid"} | Código 401, Mensagem de erro     | P0         |
| API-04 | Acesso a endpoint protegido sem token        | /api/secure/data | GET    | Nenhuma                                     | Código 401, Mensagem de erro     | P0         |
| API-05 | Acesso a endpoint protegido com token válido | /api/secure/data | GET    | Header Authorization                        | Código 200, Dados protegidos     | P0         |

### 2. Testes de Execução de Código

| ID     | Descrição                                   | Endpoint     | Método | Entradas                                                                                                                                                         | Saída Esperada                  | Prioridade |
| ------ | ------------------------------------------- | ------------ | ------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------- | ---------- |
| EXE-01 | Executar código Java simples                | /api/execute | POST   | {"code": "System.out.println(\"Hello\");"}                                                                                                                       | Código 200, Output "Hello"      | P0         |
| EXE-02 | Executar código com erro de compilação      | /api/execute | POST   | {"code": "System.out.println(\"Hello)"}                                                                                                                          | Código 400, Erro de compilação  | P0         |
| EXE-03 | Executar código com exceção em runtime      | /api/execute | POST   | {"code": "int x = 1/0;"}                                                                                                                                         | Código 500, ArithmeticException | P0         |
| EXE-04 | Executar código com múltiplas classes       | /api/execute | POST   | {"code": "class Test { public static void main(String[] args) { new Helper().run(); } } class Helper { public void run() { System.out.println(\"Helper\"); } }"} | Código 200, Output "Helper"     | P1         |
| EXE-05 | Executar código com tempo de execução longo | /api/execute | POST   | {"code": "Thread.sleep(5000);"}                                                                                                                                  | Código 200, Resposta após 5s    | P1         |

### 3. Testes de Gestão de Sessão

| ID     | Descrição                                      | Endpoint                  | Método | Entradas                                                               | Saída Esperada              | Prioridade |
| ------ | ---------------------------------------------- | ------------------------- | ------ | ---------------------------------------------------------------------- | --------------------------- | ---------- |
| SES-01 | Criar nova sessão                              | /api/session              | POST   | {"name": "testSession"}                                                | Código 201, ID da sessão    | P0         |
| SES-02 | Recuperar sessão existente                     | /api/session/{id}         | GET    | ID da sessão                                                           | Código 200, Dados da sessão | P0         |
| SES-03 | Executar código mantendo estado entre chamadas | /api/session/{id}/execute | POST   | {"code": "int x = 10; return x;"} seguido de {"code": "return x + 5;"} | Código 200, Output "15"     | P1         |
| SES-04 | Encerrar sessão                                | /api/session/{id}         | DELETE | ID da sessão                                                           | Código 204                  | P1         |
| SES-05 | Acessar sessão expirada                        | /api/session/{id}         | GET    | ID de sessão expirada                                                  | Código 404                  | P2         |

### 4. Testes de Integração com IDE

| ID     | Descrição                | Endpoint             | Método | Entradas                                           | Saída Esperada                   | Prioridade |
| ------ | ------------------------ | -------------------- | ------ | -------------------------------------------------- | -------------------------------- | ---------- |
| IDE-01 | Completar código         | /api/ide/completion  | POST   | {"code": "String s = \"hello\"; s."}               | Código 200, Sugestões de métodos | P0         |
| IDE-02 | Analisar erros de código | /api/ide/diagnostics | POST   | {"code": "String s = null; s.length();"}           | Código 200, Aviso NullPointer    | P0         |
| IDE-03 | Formatar código          | /api/ide/format      | POST   | {"code": "public class Test{void m(){int x=1;}}"}  | Código 200, Código formatado     | P1         |
| IDE-04 | Refatorar código         | /api/ide/refactor    | POST   | {"code": "...", "operation": "extractMethod"}      | Código 200, Código refatorado    | P2         |
| IDE-05 | Sugerir importações      | /api/ide/imports     | POST   | {"code": "List<String> list = new ArrayList<>();"} | Código 200, Imports sugeridos    | P1         |

### 5. Testes de Acesso a Recursos

| ID     | Descrição                              | Endpoint              | Método | Entradas                                  | Saída Esperada                      | Prioridade |
| ------ | -------------------------------------- | --------------------- | ------ | ----------------------------------------- | ----------------------------------- | ---------- |
| RES-01 | Carregar arquivo de recursos           | /api/resources/load   | POST   | {"path": "test.json"}                     | Código 200, Conteúdo do arquivo     | P1         |
| RES-02 | Salvar arquivo de recursos             | /api/resources/save   | POST   | {"path": "output.txt", "content": "test"} | Código 201                          | P1         |
| RES-03 | Listar arquivos de recursos            | /api/resources/list   | GET    | Nenhuma                                   | Código 200, Lista de arquivos       | P2         |
| RES-04 | Carregar arquivo inexistente           | /api/resources/load   | POST   | {"path": "nonexistent.txt"}               | Código 404                          | P2         |
| RES-05 | Verificar acesso a recursos protegidos | /api/resources/secure | GET    | Header Authorization                      | Código 200, Acesso permitido/negado | P0         |

## Metodologia de Execução

1. **Preparação**:

   - Configurar ambiente de teste com dados iniciais idênticos
   - Iniciar ambas as versões do servidor na mesma máquina, em portas diferentes
   - Preparar scripts de automação para execução dos testes

2. **Execução**:

   - Executar testes em ordem de dependência (básicos primeiro)
   - Executar cada teste contra ambos os servidores
   - Registrar resultados detalhados, incluindo tempo de resposta

3. **Comparação**:

   - Para cada teste, comparar:
     - Código de status HTTP
     - Estrutura do payload de resposta
     - Conteúdo específico da resposta
     - Headers relevantes (especialmente Cache-Control, Content-Type)

4. **Documentação**:
   - Registrar resultados em formato tabular
   - Destacar diferenças encontradas
   - Categorizar diferenças como:
     - Críticas (funcionalidade quebrada)
     - Moderadas (comportamento diferente mas aceitável)
     - Menores (diferenças cosméticas)

## Ferramentas de Automação

1. **JUnit 5** - Framework de teste
2. **RestAssured** - Biblioteca para testes de API REST
3. **Mockito** - Para mock de componentes externos
4. **JMeter** - Para testes de carga
5. **Jacoco** - Para cobertura de código

## Critérios de Aprovação

A validação será considerada bem-sucedida se:

1. 100% dos testes P0 passarem em ambas as versões com resultados idênticos
2. Pelo menos 95% dos testes P1 passarem em ambas as versões com resultados compatíveis
3. Nenhuma diferença crítica for identificada
4. Todas as diferenças moderadas forem documentadas e aprovadas

## Resultados e Relatórios

Os resultados dos testes serão compilados em `.manus/context/desenvolvimento_em_equipe/integration_test_results.md` e devem incluir:

1. Resumo executivo com taxa de sucesso
2. Tabela detalhada de resultados por teste
3. Análise de diferenças encontradas
4. Recomendações para correções ou melhorias
5. Métricas de performance comparativas (tempo médio de resposta por endpoint)

---

_Versão 1.0 - 20/05/2025_
