#!/usr/bin/env pwsh

# Generate Release Keystore for Android App Signing (PowerShell)
# Run this script to create a keystore for release builds

param(
    [string]$KeystoreDir = "keystore",
    [string]$KeyAlias = "popchat-release-key",
    [int]$ValidityDays = 10000
)

Write-Host "🔐 Generating Release Keystore for Pop Chat" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green
Write-Host ""

# Create keystore directory
if (-not (Test-Path $KeystoreDir)) {
    New-Item -ItemType Directory -Path $KeystoreDir | Out-Null
}

$KeystoreFile = Join-Path $KeystoreDir "release.keystore"

# Check if keystore already exists
if (Test-Path $KeystoreFile) {
    Write-Host "⚠️  Keystore already exists at $KeystoreFile" -ForegroundColor Yellow
    $response = Read-Host "Overwrite? (y/N)"
    if ($response -notmatch '^[Yy]$') {
        Write-Host "Cancelled." -ForegroundColor Red
        exit 1
    }
}

# Prompt for passwords
$KeystorePassword = Read-Host -AsSecureString "Enter keystore password"
$KeystorePasswordConfirm = Read-Host -AsSecureString "Confirm keystore password"

$KeystorePasswordPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($KeystorePassword))
$KeystorePasswordConfirmPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($KeystorePasswordConfirm))

if ($KeystorePasswordPlain -ne $KeystorePasswordConfirmPlain) {
    Write-Host "❌ Passwords do not match!" -ForegroundColor Red
    exit 1
}

$KeyPasswordSecure = Read-Host -AsSecureString "Enter key password (press Enter to use same as keystore)"
$KeyPasswordPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($KeyPasswordSecure))
if ([string]::IsNullOrEmpty($KeyPasswordPlain)) {
    $KeyPasswordPlain = $KeystorePasswordPlain
}

# Prompt for certificate details
Write-Host ""
Write-Host "📋 Enter certificate information:" -ForegroundColor Cyan
$CN = Read-Host "  First and Last Name (CN)"
$OU = Read-Host "  Organizational Unit (OU)"
$O = Read-Host "  Organization (O)"
$L = Read-Host "  City/Locality (L)"
$ST = Read-Host "  State/Province (ST)"
$C = Read-Host "  Country Code (C) [US]"
if ([string]::IsNullOrEmpty($C)) { $C = "US" }

# Generate keystore
Write-Host ""
Write-Host "🔨 Generating keystore..." -ForegroundColor Yellow

$DName = "CN=$CN, OU=$OU, O=$O, L=$L, ST=$ST, C=$C"

& keytool -genkeypair `
    -v `
    -keystore $KeystoreFile `
    -alias $KeyAlias `
    -keyalg RSA `
    -keysize 2048 `
    -validity $ValidityDays `
    -storepass $KeystorePasswordPlain `
    -keypass $KeyPasswordPlain `
    -dname $DName

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Failed to generate keystore!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "✅ Keystore generated successfully!" -ForegroundColor Green
Write-Host "📁 Location: $KeystoreFile" -ForegroundColor Cyan
Write-Host "🔑 Alias: $KeyAlias" -ForegroundColor Cyan
Write-Host ""

# Generate base64 encoded keystore for GitHub Secrets
Write-Host "📦 Generating base64 encoded keystore for GitHub Secrets..." -ForegroundColor Yellow
$KeystoreBytes = [System.IO.File]::ReadAllBytes($KeystoreFile)
$Base64Keystore = [System.Convert]::ToBase64String($KeystoreBytes)
[System.IO.File]::WriteAllText("$KeystoreFile.b64", $Base64Keystore)
Write-Host "✅ Base64 keystore saved to: $KeystoreFile.b64" -ForegroundColor Green
Write-Host ""

# Create keystore.properties
$KeystoreProps = @"
KEYSTORE_PATH=../keystore/release.keystore
KEYSTORE_PASSWORD=$KeystorePasswordPlain
KEY_ALIAS=$KeyAlias
KEY_PASSWORD=$KeyPasswordPlain
"@
[System.IO.File]::WriteAllText("keystore.properties", $KeystoreProps)
Write-Host "📝 Created keystore.properties (add to .gitignore!)" -ForegroundColor Green
Write-Host ""

Write-Host "=============================================" -ForegroundColor Green
Write-Host "📋 GitHub Secrets to configure:" -ForegroundColor Yellow
Write-Host "=============================================" -ForegroundColor Green
Write-Host "KEYSTORE_BASE64: (see $KeystoreFile.b64)" -ForegroundColor Gray
Write-Host "KEYSTORE_PASSWORD: $KeystorePasswordPlain" -ForegroundColor Gray
Write-Host "KEY_ALIAS: $KeyAlias" -ForegroundColor Gray
Write-Host "KEY_PASSWORD: $KeyPasswordPlain" -ForegroundColor Gray
Write-Host ""
Write-Host "⚠️  IMPORTANT: Save these values securely!" -ForegroundColor Red
Write-Host "   - Add them to GitHub Repository Settings > Secrets > Actions" -ForegroundColor White
Write-Host "   - Do NOT commit keystore.properties or the keystore file!" -ForegroundColor White
Write-Host ""
Write-Host "🔒 The keystore directory is already in .gitignore" -ForegroundColor Cyan