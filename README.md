# Pop Chat - Modern Android Messaging App

A feature-rich, modern Android messaging application built with **Kotlin**, **Jetpack Compose**, **Hilt**, **Room**, and **Supabase**. Designed following Material 3 guidelines with a custom "PopChat" design system.

## 🎨 Design System

- **Primary Color**: Electric Blue (#1E88E5)
- **Typography**: SF Pro style (Modern, clean, readable)
- **Shapes**: Pill-shaped buttons, circular avatars, soft-cornered bubbles/cards
- **Icons**: Minimalist line icons
- **Theme**: Light/Dark mode support

## 🏗 Architecture

```
app/
├── data/
│   ├── db/                 # Room Database
│   │   ├── dao/            # Data Access Objects
│   │   ├── converters/     # Type Converters
│   │   └── AppDatabase.kt
│   ├── model/              # Entity Models
│   ├── repository/         # Repository Interfaces & Implementations
│   └── supabase/           # Supabase Integration
│       ├── api/            # API Definitions
│       ├── model/          # Supabase Models
│       └── SupabaseClientProvider.kt
├── di/                     # Dependency Injection (Hilt Modules)
├── ui/
│   ├── auth/               # Authentication Screens
│   ├── chat/               # Chat Detail Screen
│   ├── chatlist/           # Chat List Screen
│   ├── calls/              # Voice/Video Call Screens
│   ├── discover/           # Discover People/Groups/Channels
│   ├── main/               # Main Activity & Navigation
│   ├── media/              # Shared Media Gallery
│   ├── onboarding/         # Onboarding Carousel
│   ├── profile/            # User Profile Screen
│   ├── settings/           # Settings Screen
│   ├── common/             # Reusable UI Components
│   └── theme/              # Design System (Colors, Typography, Shapes)
└── util/                   # Utilities (Logger, Result, etc.)
```

## 🛠 Tech Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin 2.0 |
| UI Framework | Jetpack Compose (Material 3) |
| Architecture | MVVM + Clean Architecture |
| Dependency Injection | Hilt |
| Local Database | Room |
| Backend | Supabase (Auth, Database, Realtime, Storage) |
| Image Loading | Coil |
| Networking | OkHttp + Retrofit + Moshi |
| Navigation | Navigation Compose |
| Async | Coroutines + Flow |
| Serialization | Kotlinx Serialization |
| Date/Time | Kotlinx Datetime |
| Logging | Timber |
| Testing | JUnit, MockK, Turbine, Compose UI Testing |

## 📱 Screens Implemented

### Onboarding & Authentication
- ✅ Splash Screen with animated logo
- ✅ Onboarding Carousel (3 slides with animations)
- ✅ Login Screen (Email/Username + Password, Google OAuth)
- ✅ Register Screen (Full Name, Email, Password, Google OAuth)
- ✅ Auth Callback Handler

### Core Chat Experience
- ✅ Chat List with filter pills (All, Friends, Groups, Channels)
- ✅ Search functionality
- ✅ 1-on-1 Chat Screen with message bubbles
- ✅ Group Chat support
- ✅ Message types: Text, Image, Video, Audio, File
- ✅ Read receipts, timestamps, reply support
- ✅ Message input bar with attachment, mic, send

### Calls
- ✅ Voice Call Screen (incoming/outgoing with animated rings)
- ✅ Video Call Screen (full-screen with PiP)
- ✅ Call History with filters (All, Missed, Outgoing, Incoming)

### Social Features
- ✅ Discover Screen (People, Groups, Channels tabs)
- ✅ User Profile with stats (Posts, Friends, Followers)
- ✅ Shared Media Gallery (Media, Files, Links tabs)

### Settings & Account
- ✅ Settings Screen (iOS-style grouped lists)
- ✅ Dark Mode toggle
- ✅ Biometric lock
- ✅ Notifications, Privacy, Security sections
- ✅ Data & Storage management

## 🚀 Getting Started

### Prerequisites
- Android Studio Koala (2024.1.1) or later
- JDK 17
- Android SDK 34
- Supabase Project

### Configuration

#### Option 1: Using .env file (Recommended for Local Development)

1. **Clone the repository**
   ```bash
   git clone https://github.com/vortexapps67/Pop-Chat.git
   cd Pop-Chat
   ```

2. **Configure Supabase with .env**
   - Copy the `.env` template or create your own:
     ```bash
     # .env file (already in .gitignore)
     SUPABASE_URL=https://your-project.supabase.co
     SUPABASE_PUBLISHABLE_KEY=your-publishable-key-here
     SUPABASE_SECRET_KEY=your-secret-key-here
     SUPABASE_JWKS_URL=https://your-project.supabase.co/auth/v1/.well-known/jwks.json
     ```
   - The build system automatically reads from `.env` first, then falls back to system environment variables.

#### Option 2: Using local.properties (Legacy)
   - Copy `local.properties.template` to `local.properties`
   - Add your Supabase credentials

3. **Configure Firebase (Optional - for Push Notifications)**
   - Create a Firebase project
   - Add `google-services.json` to `app/`

4. **Configure Signing (for Release Builds)**
   - Generate a keystore using the provided script:
     ```bash
     # Linux/macOS
     chmod +x generate-keystore.sh
     ./generate-keystore.sh
     
     # Windows PowerShell
     ./generate-keystore.ps1
     ```
   - This creates `keystore/release.keystore` and `keystore.properties`
   - The keystore directory is already in `.gitignore`

5. **Build & Run**
   ```bash
   ./gradlew assembleDebug
   ```

### GitHub Actions CI/CD

The project includes a GitHub Actions workflow (`.github/workflows/release.yml`) that:
- Builds release APK and AAB on tag push (e.g., `v1.0.0`)
- Runs lint and unit tests on pull requests
- Creates GitHub Releases with downloadable APKs

#### Required GitHub Secrets
Go to **Repository Settings → Secrets and variables → Actions** and add:

| Secret | Description |
|--------|-------------|
| `SUPABASE_URL` | Your Supabase project URL |
| `SUPABASE_PUBLISHABLE_KEY` | Supabase publishable (anon) key |
| `SUPABASE_SECRET_KEY` | Supabase secret key |
| `SUPABASE_JWKS_URL` | Supabase JWKS endpoint |
| `KEYSTORE_BASE64` | Base64-encoded release keystore (from `generate-keystore.sh`) |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias (default: `popchat-release-key`) |
| `KEY_PASSWORD` | Key password |

#### Triggering a Release
```bash
# Create and push a tag
git tag v1.0.0
git push origin v1.0.0
```

Or manually run the workflow from the **Actions** tab with a version input.

## 📦 Supabase Schema

### Tables
```sql
-- Users
CREATE TABLE users (
    id UUID PRIMARY KEY REFERENCES auth.users(id),
    email TEXT,
    username TEXT UNIQUE,
    display_name TEXT,
    avatar_url TEXT,
    status TEXT DEFAULT 'online',
    last_seen TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Chats
CREATE TABLE chats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT,
    avatar_url TEXT,
    is_group BOOLEAN DEFAULT FALSE,
    created_by UUID REFERENCES users(id),
    last_message_id UUID,
    last_message_preview TEXT,
    last_message_at TIMESTAMPTZ,
    unread_count INTEGER DEFAULT 0,
    is_archived BOOLEAN DEFAULT FALSE,
    is_pinned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Messages
CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chat_id UUID REFERENCES chats(id) ON DELETE CASCADE,
    sender_id UUID REFERENCES users(id),
    content TEXT,
    type TEXT DEFAULT 'text', -- text, image, video, audio, file, location, contact, system
    media_url TEXT,
    media_type TEXT,
    media_size BIGINT,
    reply_to_id UUID REFERENCES messages(id),
    is_edited BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    delivered_at TIMESTAMPTZ,
    read_at TIMESTAMPTZ
);

-- Chat Participants
CREATE TABLE chat_participants (
    chat_id UUID REFERENCES chats(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    role TEXT DEFAULT 'member', -- owner, admin, member
    joined_at TIMESTAMPTZ DEFAULT NOW(),
    last_read_message_id UUID,
    is_muted BOOLEAN DEFAULT FALSE,
    muted_until TIMESTAMPTZ,
    PRIMARY KEY (chat_id, user_id)
);
```

### Row Level Security (RLS)
Enable RLS on all tables and create policies for:
- Users can read/write their own data
- Chat participants can read/write messages in their chats
- Users can read profiles of people they share chats with

### Realtime
Enable Supabase Realtime on `messages` and `chats` tables for live updates.

## 🧪 Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# All tests
./gradlew check
```

## 📦 Build Variants

- **Debug**: Application ID `com.popchat.debug`, debuggable, no minification
- **Release**: Application ID `com.popchat`, minified with R8, signed

## 📄 License

```
Copyright 2024 Pop Chat

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📞 Support

For issues and feature requests, please use the GitHub issue tracker.