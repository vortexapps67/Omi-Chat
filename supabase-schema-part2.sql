-- Pop Chat Supabase Database Schema - PART 2: Realtime Publication
-- Run this SECOND (after confirming all tables exist)

-- ============================================================
-- REALTIME PUBLICATION
-- ============================================================
-- Enable realtime for messages, chats, and participants
-- NOTE: Run this only after all tables from Part 1 are created successfully

ALTER PUBLICATION supabase_realtime ADD TABLE messages;
ALTER PUBLICATION supabase_realtime ADD TABLE chats;
ALTER PUBLICATION supabase_realtime ADD TABLE chat_participants;

-- Verify realtime is enabled
SELECT * FROM pg_publication_tables WHERE pubname = 'supabase_realtime';