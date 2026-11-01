-- V4__add_tour_version_column.sql
-- Ensures version column exists on tour table (idempotent)
-- Related: LLD Section 5.2, 5.4
-- Note: version column is already created in V1. This migration is a safety net.

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'tour' AND column_name = 'version'
    ) THEN
        ALTER TABLE tour ADD COLUMN version INTEGER NOT NULL DEFAULT 0;
    END IF;
END
$$;
