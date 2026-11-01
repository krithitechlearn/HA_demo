-- DML for initial data population

-- Insert default db_admin user
-- Password will be hashed by the application on first run or provided via environment variable.
-- For initial setup, a placeholder password hash is used.
-- In a real scenario, this would be handled by Flyway seed scripts with environment variable injection.

INSERT INTO app_user (user_id, email, username, password_hash, enabled, created_by, updated_by)
VALUES (
    '00000000-0000-0000-0000-000000000001', -- Example UUID for admin
    'admin@example.com',
    'db_admin',
    '$2a$12$EXAMPLEHASHFORADMINPASSWORD.EXAMPLEHASHFORADMINPASSWORD.', -- Placeholder for a bcrypt hash of a default password
    TRUE,
    'system',
    'system'
)
ON CONFLICT (username) DO NOTHING; -- Prevent re-insertion if script is run multiple times

-- Assign ROLE_DB_ADMIN to the default admin user
INSERT INTO user_role (user_id, role)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'ROLE_DB_ADMIN'
)
ON CONFLICT (user_id, role) DO NOTHING;