#!/usr/bin/env pwsh

# Push Pop Chat to GitHub (PowerShell version)
# Repository: https://github.com/vortexapps67/Pop-Chat

param(
    [string]$Branch = "main",
    [string]$RepoUrl = "https://github.com/vortexapps67/Pop-Chat.git"
)

Write-Host "🚀 Pushing Pop Chat to GitHub..." -ForegroundColor Green
Write-Host "Repository: $RepoUrl" -ForegroundColor Cyan
Write-Host "Branch: $Branch" -ForegroundColor Cyan
Write-Host ""

# Check if git is initialized
if (-not (Test-Path ".git")) {
    Write-Host "📦 Initializing git repository..." -ForegroundColor Yellow
    git init
    git branch -M $Branch
}

# Check if remote exists
try {
    $existingRemote = git remote get-url origin
    if ($existingRemote -ne $RepoUrl) {
        Write-Host "🔗 Updating remote origin..." -ForegroundColor Yellow
        git remote set-url origin $RepoUrl
    } else {
        Write-Host "🔗 Remote origin already exists" -ForegroundColor Cyan
    }
} catch {
    Write-Host "🔗 Adding remote origin..." -ForegroundColor Yellow
    git remote add origin $RepoUrl
}

# Add all files
Write-Host "📝 Adding files to git..." -ForegroundColor Yellow
git add .

# Check if there are changes to commit
$status = git status --porcelain
if (-not $status) {
    Write-Host "⚠️  No changes to commit" -ForegroundColor Yellow
} else {
    Write-Host "💾 Committing changes..." -ForegroundColor Yellow
    $commitMessage = @"
feat: Initial Pop Chat Android app with Supabase backend

- Kotlin + Jetpack Compose + Hilt + Room + Supabase
- Onboarding, Auth (Login/Register), Chat List, Chat Detail
- Voice/Video Calls, Discover, Profile, Settings
- Shared Media Gallery, Call History
- Material 3 Design System with Electric Blue theme
- GitHub Actions workflow for release APK builds
- .env configuration for Supabase credentials
"@
    git commit -m $commitMessage
}

# Push to GitHub
Write-Host "📤 Pushing to GitHub..." -ForegroundColor Yellow
git push -u origin $Branch --force

Write-Host ""
Write-Host "✅ Successfully pushed to GitHub!" -ForegroundColor Green
Write-Host "🔗 Repository: https://github.com/vortexapps67/Pop-Chat" -ForegroundColor Cyan
Write-Host ""
Write-Host "📋 Next steps:" -ForegroundColor Yellow
Write-Host "1. Go to GitHub repository settings" -ForegroundColor White
Write-Host "2. Add the following secrets for GitHub Actions:" -ForegroundColor White
Write-Host "   - SUPABASE_URL" -ForegroundColor Gray
Write-Host "   - SUPABASE_PUBLISHABLE_KEY" -ForegroundColor Gray
Write-Host "   - SUPABASE_SECRET_KEY" -ForegroundColor Gray
Write-Host "   - SUPABASE_JWKS_URL" -ForegroundColor Gray
Write-Host "   - KEYSTORE_BASE64 (base64 encoded keystore)" -ForegroundColor Gray
Write-Host "   - KEYSTORE_PASSWORD" -ForegroundColor Gray
Write-Host "   - KEY_ALIAS" -ForegroundColor Gray
Write-Host "   - KEY_PASSWORD" -ForegroundColor Gray
Write-Host "3. Create a release tag (e.g., v1.0.0) to trigger APK build" -ForegroundColor White
Write-Host "4. Or manually run the 'Build and Release APK' workflow" -ForegroundColor White