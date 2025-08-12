@echo off
setlocal enabledelayedexpansion

echo ========================================
echo Iniciando aplicacao Fechamento de Caixa...
echo ========================================
echo.

REM Configuracoes
REM Variaveis (pode sobrescrever via ambiente/Jenkins)
set SERVER_PORT=8081
set CONTEXT_PATH=/api
REM Preferir application.yml local (Mongo local). Para Atlas, definir SPRING_DATA_MONGODB_URI no ambiente.

echo Configuracoes:
echo - Porta: %SERVER_PORT%
echo - Context Path: %CONTEXT_PATH%
echo.

REM Compilando projeto
echo Compilando projeto...
call mvnw.cmd clean compile -q
if %errorlevel% neq 0 (
    echo Erro na compilacao! Verifique os logs acima.
    pause
    exit /b 1
)
echo Compilacao bem-sucedida!
echo.

REM Executar aplicacao
echo Iniciando servidor...
echo Acesse: http://localhost:%SERVER_PORT%%CONTEXT_PATH%
echo.

REM Executar usando Spring Boot Maven Plugin
call mvnw.cmd spring-boot:run ^
    -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev -Dserver.port=%SERVER_PORT% -Dserver.servlet.context-path=%CONTEXT_PATH% --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED"

if %errorlevel% neq 0 (
    echo.
    echo Erro ao iniciar a aplicacao!
    echo Verifique os logs acima para mais detalhes.
)

pause
