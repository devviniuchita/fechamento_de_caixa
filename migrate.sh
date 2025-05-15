#!/bin/bash

# Remove arquivos duplicados do pacote com.controle.caixa
rm -rf src/main/java/com/controle
rm -rf src/test/java/com/controle

# Criar diretórios necessários para frontend
mkdir -p src/main/resources/static/js
mkdir -p src/main/resources/static/css
mkdir -p src/main/resources/static/img
mkdir -p src/main/resources/templates

# Mover arquivos frontend para locais apropriados
mv index.html src/main/resources/templates/
mv public/js/* src/main/resources/static/js/
mv public/css/* src/main/resources/static/css/
mv public/img/* src/main/resources/static/img/

# Limpar diretórios vazios
rm -rf public 