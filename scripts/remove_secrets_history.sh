#!/usr/bin/env bash
set -euo pipefail

cat <<'EOF'
WARNING: This script rewrites git history. All contributors must coordinate before running it.
It uses `git filter-repo` to remove sensitive files and tokens from history.
Read documentation: https://github.com/newren/git-filter-repo
Alternatives: BFG Repo-Cleaner.
EOF

# Example: remove .env, any file named secrets.*, or common patterns
# Install git-filter-repo first: pip3 install git-filter-repo

if ! command -v git-filter-repo >/dev/null 2>&1; then
  echo "Please install git-filter-repo (pip3 install git-filter-repo) and re-run this script."
  exit 1
fi

read -p "You are about to rewrite git history. Type the branch name to operate on (default: main): " TARGET_BRANCH
TARGET_BRANCH=${TARGET_BRANCH:-main}

# Create a backup tag
git tag -a pre-filter-$(date +%Y%m%d%H%M%S) -m "Backup before secret removal"

# Remove files from history
git filter-repo --invert-paths --paths .env --paths .env.local --paths .env.* --force

# Remove common secret patterns (example - add more patterns as needed)
# For more advanced pattern removal consult git-filter-repo docs

cat <<'EOF'
Done. You must push with force and coordinate with collaborators:
  git push --force --all
  git push --force --tags
Also, rotate any credentials that may have been exposed.
EOF
