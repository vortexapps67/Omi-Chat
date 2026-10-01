#!/bin/bash

# Generate Release Keystore for Android App Signing
# Run this script to create a keystore for release builds

set -e

KEYSTORE_DIR="keystore"
KEYSTORE_FILE="$KEYSTORE_DIR/release.keystore"
KEY_ALIAS="popchat-release-key"
VALIDITY_DAYS=10000  # ~27 years

echo "🔐 Generating Release Keystore for Pop Chat"
echo "============================================="
echo ""

# Create keystore directory
mkdir -p $KEYSTORE_DIR

# Check if keystore already exists
if [ -f "$KEYSTORE_FILE" ]; then
    echo "⚠️  Keystore already exists at $KEYSTORE_FILE"
    read -p "Overwrite? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Cancelled."
        exit 1
    fi
fi

# Prompt for passwords
read -s -p "Enter keystore password: " KEYSTORE_PASSWORD
echo
read -s -p "Confirm keystore password: " KEYSTORE_PASSWORD_CONFIRM
echo
if [ "$KEYSTORE_PASSWORD" != "$KEYSTORE_PASSWORD_CONFIRM" ]; then
    echo "❌ Passwords do not match!"
    exit 1
fi

read -s -p "Enter key password (can be same as keystore): " KEY_PASSWORD
echo
if [ -z "$KEY_PASSWORD" ]; then
    KEY_PASSWORD="$KEYSTORE_PASSWORD"
fi

# Prompt for certificate details
echo ""
echo "📋 Enter certificate information:"
read -p "  First and Last Name (CN): " CN
read -p "  Organizational Unit (OU): " OU
read -p "  Organization (O): " O
read -p "  City/Locality (L): " L
read -p "  State/Province (ST): " ST
read -p "  Country Code (C) [US]: " C
C=${C:-US}

# Generate keystore
echo ""
echo "🔨 Generating keystore..."
keytool -genkeypair \
    -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$KEY_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity $VALIDITY_DAYS \
    -storepass "$KEYSTORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "CN=$CN, OU=$OU, O=$O, L=$L, ST=$ST, C=$C"

echo ""
echo "✅ Keystore generated successfully!"
echo "📁 Location: $KEYSTORE_FILE"
echo "🔑 Alias: $KEY_ALIAS"
echo ""

# Generate base64 encoded keystore for GitHub Secrets
echo "📦 Generating base64 encoded keystore for GitHub Secrets..."
base64 -i "$KEYSTORE_FILE" -o "$KEYSTORE_FILE.b64"
echo "✅ Base64 keystore saved to: $KEYSTORE_FILE.b64"
echo ""

# Create keystore.properties
cat > keystore.properties << EOF
KEYSTORE_PATH=../keystore/release.keystore
KEYSTORE_PASSWORD=$KEYSTORE_PASSWORD
KEY_ALIAS=$KEY_ALIAS
KEY_PASSWORD=$KEY_PASSWORD
EOF

echo "📝 Created keystore.properties (add to .gitignore!)"
echo ""

echo "============================================="
echo "📋 GitHub Secrets to configure:"
echo "============================================="
echo "KEYSTORE_BASE64: $(cat $KEYSTORE_FILE.b64)"
echo "KEYSTORE_PASSWORD: $KEYSTORE_PASSWORD"
echo "KEY_ALIAS: $KEY_ALIAS"
echo "KEY_PASSWORD: $KEY_PASSWORD"
echo ""
echo "⚠️  IMPORTANT: Save these values securely!"
echo "   - Add them to GitHub Repository Settings > Secrets > Actions"
echo "   - Do NOT commit keystore.properties or the keystore file!"
echo ""
echo "🔒 The keystore directory is already in .gitignore"