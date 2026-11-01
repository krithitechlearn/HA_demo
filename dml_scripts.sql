-- DML for Shore Excursions Application Modernization - Seed Data

-- Insert default db_admin user
-- Password should be hashed using BCrypt (strength 12) and provided via environment variable ADMIN_DEFAULT_PASSWORD.
-- The application will handle hashing this password on initial setup or via Flyway V2 script.
-- For a direct SQL insert, you would need the already hashed password.
-- Example placeholder:
-- INSERT INTO app_user (user_id, email, username, password_hash, enabled, created_by, updated_by)
-- VALUES (gen_random_uuid(), 'admin@example.com', 'db_admin', '$2a$12$EXAMPLEHASHFORADMINPASSWORD', TRUE, 'system', 'system')
-- ON CONFLICT (username) DO NOTHING;

-- This script represents the initial data, actual admin user creation is handled by Flyway V2.
-- We will provide a note in Migration Notes for this.

-- No other initial data specified in the LLD for direct DML.
-- Legacy role mapping is a reference and not direct DML for initial setup.