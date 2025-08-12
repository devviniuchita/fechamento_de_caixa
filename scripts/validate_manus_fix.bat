@echo off
setlocal enabledelayedexpansion

echo ====================================
echo Validação da API Fechamento de Caixa
echo ID do problema: 50e4a31d-3ef9-4c6b-bffc-2b8420a022e1
echo ====================================
echo.

REM Verificar configurações
echo Verificando configurações...
set CONFIG_ERRORS=0

REM Verificar porta no application.properties
findstr /C:"server.port=9091" application.properties > nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ application.properties: Porta configurada corretamente (9091)
) else (
    echo ❌ application.properties: Porta não configurada corretamente
    set /a CONFIG_ERRORS+=1
)

REM Verificar context-path no application.yml
findstr /C:"context-path: /" application.yml > nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ application.yml: Context-path configurado corretamente (/)
) else (
    echo ❌ application.yml: Context-path não configurado corretamente
    set /a CONFIG_ERRORS+=1
)

REM Verificar existência dos arquivos de recuperação
if exist .manus\recovery_config.yml (
    echo ✅ Arquivo de recuperação encontrado
) else (
    echo ❌ Arquivo de recuperação não encontrado
    set /a CONFIG_ERRORS+=1
)

REM Verificar script de reparo
if exist scripts\manus_repair.bat (
    echo ✅ Script de reparo encontrado
) else (
    echo ❌ Script de reparo não encontrado
    set /a CONFIG_ERRORS+=1
)

echo.
echo Tentando conectar a API Fechamento de Caixa...

REM Tenta conectar ao servidor em várias portas
set "SERVER_PORT="
for %%p in (9091 9092 9093 9094 9095 8080 8081 8082) do (
    echo -n Verificando porta %%p: 
    curl -s http://localhost:%%p/health > nul 2>&1
    if !ERRORLEVEL! EQU 0 (
        echo ✅ Servidor disponível!
        set "SERVER_PORT=%%p"
        goto :server_found
    ) else (
        echo ❌ Servidor não disponível
    )
)

:server_found
echo.
if defined SERVER_PORT (
    echo API encontrada na porta !SERVER_PORT!
    echo Enviando comando de teste...
    curl -s -I http://localhost:!SERVER_PORT!/api | findstr /C:"HTTP/"
) else (
    echo API não encontrada nas portas verificadas!
    echo Tentar iniciar automaticamente? (S/N)
    set /p answer=
    if /i "!answer!"=="s" (
        echo Iniciando API...
        start /B mvn spring-boot:run > .manus\logs\api_server.log 2>&1
        echo Servidor iniciado em background. Verifique .manus\logs\api_server.log
    )
)

echo.
echo Verificando integração com Cursor...
findstr /S /C:"manus-cursor-spring" *.json
if %ERRORLEVEL% NEQ 0 echo Configuração do Cursor para Manus não encontrada

echo.
echo Resumo da validação:
if %CONFIG_ERRORS% EQU 0 if defined SERVER_PORT (
    echo ✅ SOLUÇÃO VALIDADA: O problema 50e4a31d-3ef9-4c6b-bffc-2b8420a022e1 parece estar corrigido!
    echo   - Configurações ajustadas corretamente
    echo   - API disponível na porta !SERVER_PORT!
    echo   - Arquivos de recuperação criados
    
    REM Registrar solução como bem-sucedida
    if not exist .manus\solved mkdir .manus\solved
    echo %date% %time%: Problema 50e4a31d-3ef9-4c6b-bffc-2b8420a022e1 resolvido > .manus\solved\50e4a31d.txt
) else (
    echo ⚠️ SOLUÇÃO PARCIAL: Ainda há itens a serem verificados ou corrigidos:
    echo   - Erros de configuração: %CONFIG_ERRORS%
    echo   - API disponível: !SERVER_PORT!
    echo.
    echo Execute 'scripts\manus_repair.bat' para tentar reparar esses problemas.
)

echo.
echo ====================================