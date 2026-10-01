#!/bin/bash

# Push Pop Chat to GitHub
# Repository: https://github.com/vortexapps67/Pop-Chat

set -e

REPO_URL="https://github.com/vortexapps67/Pop-Chat.git"
BRANCH="main"

echo "🚀 Pushing Pop Chat to GitHub..."
echo "Repository: $REPO_URL"
echo "Branch: $BRANCH"
echo ""

# Check if git is initialized
if [ ! -d ".git" ]; then
    echo "📦 Initializing git repository..."
    git init
    git branch -M $BRANCH
fi

# Check if remote exists
if ! git remote get-url origin > /dev/null 2>&1; then
    echo "🔗 Adding remote origin..."
    git remote add origin $REPO_URL
else
    echo "🔗 Remote origin already exists"
    git remote set-url origin $REPO_URL
fi

# Add all files
echo "📝 Adding files to git..."
git add .

# Check if there are changes to commit
if git diff --cached --quiet; then
    echo "⚠️  No changes to commit"
else
    echo "💾 Committing changes..."
    git commit -m "feat: Initial Pop Chat Android app with Supabase backend

- Kotlin + Jetpack Compose + Hilt + Room + Supabase
- Onboarding, Auth (Login/Register), Chat List, Chat Detail
- Voice/Video Calls, Discover, Profile, Settings
- Shared Media Gallery, Call History
- Material 3 Design System with Electric Blue theme
- GitHub Actions workflow for release APK builds
- .env configuration for Supabase credentials"
fi

# Push to GitHub
echo "📤 Pushing to GitHub..."
git push -u origin $BRANCH --force

echo ""
echo "✅ Successfully pushed to GitHub!"
echo "🔗 Repository: https://github.com/vortexapps67/Pop-Chat"
echo ""
echo "📋 Next steps:"
echo "1. Go to GitHub repository settings"
echo "2. Add the following secrets for GitHub Actions:"
echo "   - SUPABASE_URL"
echo "   - SUPABASE_PUBLISHABLE_KEY"
echo "   - SUPABASE_SECRET_KEY"
echo "   - SUPABASE_JWKS_URL"
echo "   - KEYSTORE_BASE64 (base64 encoded keystore)"
echo "   - KEYSTORE_PASSWORD"
echo "   - KEY_ALIAS"
echo "   - KEY_PASSWORD"
echo "3. Create a release tag (e.g., v1.0.0) to trigger APK build"
echo "4. Or manually run the 'Build and Release APK' workflow"