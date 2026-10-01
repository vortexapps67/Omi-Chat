-- Pop Chat Supabase Database Schema - Complete, Single-File Execution
-- Run this entire file in Supabase SQL Editor (https://supabase.com/dashboard/project/_/sql/new)
-- Tables are created FIRST, then policies, then realtime

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- STEP 1: CREATE ALL TABLES (no cross-table policies yet)
-- ============================================================

-- USERS TABLE
CREATE TABLE users (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT,
    username TEXT UNIQUE,
    display_name TEXT,
    avatar_url TEXT,
    status TEXT DEFAULT 'online',
    last_seen TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- CHATS TABLE
CREATE TABLE chats (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT,
    avatar_url TEXT,
    is_group BOOLEAN DEFAULT FALSE,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    last_message_id UUID,
    last_message_preview TEXT,
    last_message_at TIMESTAMPTZ,
    unread_count INTEGER DEFAULT 0,
    is_archived BOOLEAN DEFAULT FALSE,
    is_pinned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- MESSAGES TABLE
CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    chat_id UUID REFERENCES chats(id) ON DELETE CASCADE,
    sender_id UUID REFERENCES users(id) ON DELETE SET NULL,
    content TEXT,
    type TEXT DEFAULT 'text',
    media_url TEXT,
    media_type TEXT,
    media_size BIGINT,
    reply_to_id UUID REFERENCES messages(id) ON DELETE SET NULL,
    is_edited BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    delivered_at TIMESTAMPTZ,
    read_at TIMESTAMPTZ
);

-- CHAT PARTICIPANTS TABLE
CREATE TABLE chat_participants (
    chat_id UUID REFERENCES chats(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    role TEXT DEFAULT 'member',
    joined_at TIMESTAMPTZ DEFAULT NOW(),
    last_read_message_id UUID REFERENCES messages(id) ON DELETE SET NULL,
    is_muted BOOLEAN DEFAULT FALSE,
    muted_until TIMESTAMPTZ,
    PRIMARY KEY (chat_id, user_id)
);

-- ============================================================
-- STEP 2: ENABLE RLS ON ALL TABLES
-- ============================================================
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE chats ENABLE ROW LEVEL SECURITY;
ALTER TABLE messages ENABLE ROW LEVEL SECURITY;
ALTER TABLE chat_participants ENABLE ROW LEVEL SECURITY;

-- ============================================================
-- STEP 3: CREATE POLICIES (now all tables exist)
-- ============================================================

-- USERS POLICIES
CREATE POLICY "Users can view own profile" ON users
    FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update own profile" ON users
    FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Users can insert own profile" ON users
    FOR INSERT WITH CHECK (auth.uid() = id);

-- Users can view profiles of people in shared chats
CREATE POLICY "Users can view profiles in shared chats" ON users
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM chat_participants cp1
            JOIN chat_participants cp2 ON cp1.chat_id = cp2.chat_id
            WHERE cp1.user_id = auth.uid() AND cp2.user_id = users.id
        )
    );

-- CHATS POLICIES
CREATE POLICY "Participants can view their chats" ON chats
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM chat_participants
            WHERE chat_id = chats.id AND user_id = auth.uid()
        )
    );

CREATE POLICY "Participants can update their chats" ON chats
    FOR UPDATE USING (
        EXISTS (
            SELECT 1 FROM chat_participants
            WHERE chat_id = chats.id AND user_id = auth.uid()
        )
    );

CREATE POLICY "Users can create chats" ON chats
    FOR INSERT WITH CHECK (auth.uid() = created_by);

-- MESSAGES POLICIES
CREATE POLICY "Participants can view messages in their chats" ON messages
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM chat_participants
            WHERE chat_id = messages.chat_id AND user_id = auth.uid()
        )
    );

CREATE POLICY "Participants can insert messages in their chats" ON messages
    FOR INSERT WITH CHECK (
        EXISTS (
            SELECT 1 FROM chat_participants
            WHERE chat_id = messages.chat_id AND user_id = auth.uid()
        )
        AND sender_id = auth.uid()
    );

CREATE POLICY "Sender can update own messages" ON messages
    FOR UPDATE USING (sender_id = auth.uid());

CREATE POLICY "Sender can delete own messages" ON messages
    FOR DELETE USING (sender_id = auth.uid());

-- CHAT PARTICIPANTS POLICIES
CREATE POLICY "Participants can view participants in their chats" ON chat_participants
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM chat_participants cp
            WHERE cp.chat_id = chat_participants.chat_id AND cp.user_id = auth.uid()
        )
    );

CREATE POLICY "Chat owners/admins can add participants" ON chat_participants
    FOR INSERT WITH CHECK (
        EXISTS (
            SELECT 1 FROM chat_participants cp
            WHERE cp.chat_id = chat_participants.chat_id 
            AND cp.user_id = auth.uid() 
            AND cp.role IN ('owner', 'admin')
        )
    );

CREATE POLICY "Chat owners/admins can update participants" ON chat_participants
    FOR UPDATE USING (
        EXISTS (
            SELECT 1 FROM chat_participants cp
            WHERE cp.chat_id = chat_participants.chat_id 
            AND cp.user_id = auth.uid() 
            AND cp.role IN ('owner', 'admin')
        )
    );

CREATE POLICY "Users can leave chats" ON chat_participants
    FOR DELETE USING (user_id = auth.uid());

CREATE POLICY "Chat owners can remove participants" ON chat_participants
    FOR DELETE USING (
        EXISTS (
            SELECT 1 FROM chat_participants cp
            WHERE cp.chat_id = chat_participants.chat_id 
            AND cp.user_id = auth.uid() 
            AND cp.role = 'owner'
        )
    );

-- ============================================================
-- STEP 4: INDEXES FOR PERFORMANCE
-- ============================================================
CREATE INDEX idx_messages_chat_id_created_at ON messages(chat_id, created_at DESC);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_reply_to_id ON messages(reply_to_id);
CREATE INDEX idx_chat_participants_user_id ON chat_participants(user_id);
CREATE INDEX idx_chats_last_message_at ON chats(last_message_at DESC NULLS LAST);
CREATE INDEX idx_users_username ON users(username);

-- ============================================================
-- STEP 5: TRIGGERS FOR UPDATED_AT
-- ============================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_chats_updated_at BEFORE UPDATE ON chats
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_messages_updated_at BEFORE UPDATE ON messages
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- STEP 6: REALTIME PUBLICATION (last)
-- ============================================================
ALTER PUBLICATION supabase_realtime ADD TABLE messages;
ALTER PUBLICATION supabase_realtime ADD TABLE chats;
ALTER PUBLICATION supabase_realtime ADD TABLE chat_participants;

-- ============================================================
-- VERIFICATION QUERIES (run after to confirm)
-- ============================================================
-- Check all tables exist
-- SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';

-- Check realtime publication
-- SELECT * FROM pg_publication_tables WHERE pubname = 'supabase_realtime';

-- Check RLS policies
-- SELECT * FROM pg_policies WHERE schemaname = 'public';