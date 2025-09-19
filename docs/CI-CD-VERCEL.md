# CI/CD com Vercel (Preview e Production)

Este projeto usa um workflow do GitHub Actions para publicar automaticamente no Vercel:

- Preview em Pull Requests que apontem para a branch main
- Production em pushes diretos na branch main

O deploy usa a Vercel CLI e só é executado se os segredos estiverem configurados no repositório.

## Segredos necessários (GitHub → Settings → Secrets and variables → Actions)

Crie três segredos em “Repository secrets” com estes nomes exatos:

- VERCEL_TOKEN: Token de acesso da sua conta (Vercel Personal Token)
- VERCEL_ORG_ID: ID da organização no Vercel
- VERCEL_PROJECT_ID: ID do projeto no Vercel

Como obter:

- VERCEL_TOKEN: Dashboard Vercel → Account Settings → Tokens → Create Token
- VERCEL_ORG_ID e VERCEL_PROJECT_ID:
  - Pelo Dashboard: Abra o projeto, copie os IDs mostrados em Settings → General
  - Pela CLI (com login feito): vercel whoami --scope <org> e vercel projects ls

Observação: Não commit suas credenciais; use sempre GitHub Secrets.

## Como funciona o workflow

Arquivo: .github/workflows/vercel-deploy.yml

Gatilhos:

- pull_request para main → job preview (comentará a URL de preview no PR)
- push na main → job production (deploy com --prod)

Comportamento sem segredos: Se qualquer um dos segredos não estiver presente, os passos de deploy serão pulados e o job registrará mensagem de “Skipping… missing secrets”.

## Estrutura enviada ao Vercel

Existe um .vercelignore na raiz para reduzir o upload (evitando pastas pesadas como src/, target/, node_modules/, .git/ etc.).

- Hoje, isso publica apenas os artefatos estáticos na raiz (ex.: index.html) e a pasta public/.
- Isso é útil para um preview estático do front (landing/demo). Não é um deploy do backend Spring Boot.

Se você quiser publicar um front-end buildado (ex.: Next.js/Vite) ou outra pasta, ajuste o comando vercel deploy e/ou remova/edite o .vercelignore conforme sua estratégia.

## Passo a passo de validação

1. Configurar os segredos (VERCEL_TOKEN, VERCEL_ORG_ID, VERCEL_PROJECT_ID)
2. Criar um Pull Request apontando de uma branch de feature para main
   - Espere o job “Preview (PR)”. Se os segredos estiverem ok, ele comentará a URL de preview no PR
3. Fazer um push/merge na main
   - Espere o job “Production (main)”. Se os segredos estiverem ok, haverá um deploy de produção

## Troubleshooting

- Segredos ausentes: O job mostrará “Skipping Vercel …: missing secrets”. Configure os 3 segredos e reexecute (reopen PR ou novo push)
- Autenticação/Permissões: Revogue e gere um novo VERCEL_TOKEN; confira o scope/organização
- Projeto/Org incorretos: Verifique VERCEL_ORG_ID e VERCEL_PROJECT_ID no dashboard do Vercel
- Upload inesperado de arquivos: Ajuste .vercelignore. Lembre: caminhos iniciados com / são relativos à raiz do repositório
- Uso de backend Spring Boot: Este workflow não publica o backend no Vercel; é focado em artefatos estáticos. Para backend Java, prefira um provedor de apps (Azure App Service, Render, Fly.io, etc.) ou mantenha deploy separado

## Próximos passos (opcional)

- Unificar comentários de Preview no PR (dedupe) via actions/github-script
- Monorepo: apontar “rootDir”/“outputDir” se houver front-end dedicado
- Variáveis de ambiente: usar “vercel env pull” conforme necessário no projeto front
