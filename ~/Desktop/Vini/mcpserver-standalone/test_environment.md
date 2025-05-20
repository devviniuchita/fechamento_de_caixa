# Ambiente de Teste Compartilhado - MCP Server

## Visão Geral

Este documento descreve como configurar e utilizar o ambiente de teste compartilhado para validação do MCP Server. O ambiente permite que todos os membros da Equipe executem testes de validação de endpoints e testes de integração de forma padronizada.

## Requisitos

- Java 11 ou superior
- Maven 3.6 ou superior
- Git Bash ou outro terminal compatível com scripts bash
- Portas 8080 e 8081 disponíveis

## Configuração do Ambiente

### 1. Configuração do Projeto Original

```bash
# Clonar o repositório (se ainda não tiver feito)
git clone https://github.com/seu-usuario/fechamento-de-caixa.git
cd fechamento-de-caixa

# Compilar o projeto
mvn clean package -DskipTests

# Iniciar o servidor na porta 8080
java -jar target/fechamento-caixa-1.0.0.jar --server.port=8080
```

### 2. Configuração do MCP Server Standalone

```bash
# Navegar para o diretório do projeto standalone
cd ~/Desktop/Vini/mcpserver-standalone

# Compilar o projeto
mvn clean package -DskipTests

# Iniciar o servidor na porta 8081
java -jar target/mcpserver-standalone-1.0.0-SNAPSHOT.jar --server.port=8081
```

## Execução dos Testes

### Validação de Endpoints

#### No Windows:

```bash
cd ~/Desktop/Vini/mcpserver-standalone/scripts
./validate_endpoints_win.sh http://localhost:8080 http://localhost:8081
```

#### No Linux/Mac:

```bash
cd ~/Desktop/Vini/mcpserver-standalone/scripts
./validate_endpoints.sh http://localhost:8080 http://localhost:8081
```

### Testes de Integração

```bash
cd ~/Desktop/Vini/mcpserver-standalone
mvn test -Dtest=EndpointCompatibilityTest -Dcompatibility.test=true -Doriginal.server.url=http://localhost:8080 -Dnew.server.url=http://localhost:8081
```

### Testes de Performance

```bash
cd ~/Desktop/Vini/mcpserver-standalone
mvn test -Dtest=PerformanceComparisonTest -Dperformance.test=true -Doriginal.server.url=http://localhost:8080 -Dnew.server.url=http://localhost:8081
```

## Verificação dos Resultados

Após a execução dos testes, os resultados estarão disponíveis nos seguintes locais:

- **Validação de Endpoints**:

  - Relatório: `~/Desktop/Vini/mcpserver-standalone/scripts/endpoint_validation_report.md`
  - Log: `~/Desktop/Vini/mcpserver-standalone/scripts/endpoint_validation_results.log`

- **Testes de Integração**:

  - Resultados: `~/Desktop/Vini/mcpserver-standalone/integration_test_results.md`
  - Log: `~/Desktop/Vini/mcpserver-standalone/target/surefire-reports/com.mcpserver.EndpointCompatibilityTest.txt`

- **Testes de Performance**:
  - Resultados: `~/Desktop/Vini/mcpserver-standalone/performance_results.md`
  - Log: `~/Desktop/Vini/mcpserver-standalone/target/surefire-reports/com.mcpserver.PerformanceComparisonTest.txt`

## Resolução de Problemas

### Portas em Uso

Se as portas 8080 ou 8081 estiverem em uso, você pode alterar as portas:

```bash
# Para o projeto original
java -jar target/fechamento-caixa-1.0.0.jar --server.port=8082

# Para o MCP Server standalone
java -jar target/mcpserver-standalone-1.0.0-SNAPSHOT.jar --server.port=8083

# Ajuste os scripts de teste para usar as novas portas
./validate_endpoints_win.sh http://localhost:8082 http://localhost:8083
```

### Falha na Conexão

Se houver falha na conexão com os servidores:

1. Verifique se ambos os servidores estão em execução
2. Verifique se as portas estão corretas
3. Verifique se não há firewall bloqueando as conexões

### Erros de Compilação

Se houver erros de compilação:

```bash
# Limpar o projeto e recompilar
mvn clean package -DskipTests
```

## Contato para Suporte

Em caso de problemas na configuração do ambiente de teste, entre em contato com a Equipe via protocolo SLI:

```bash
./.manus/scripts/communication.sh enviar "seu-nome" "equipe" "suporte" "Descrição do problema encontrado" "true"
```

---

_Última atualização: 2024-07-03_
