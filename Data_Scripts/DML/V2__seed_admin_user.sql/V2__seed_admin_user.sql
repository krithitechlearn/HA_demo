-- DML for initial data population
-- Insert default db_admin user. Password should be hashed (e.g., BCrypt) before insertion.
-- The password_hash below is a placeholder. Replace with an actual BCrypt hash.
INSERT INTO app_user (user_id, email, username, password_hash, enabled, created_by, updated_by)
VALUES (gen_random_uuid(), 'admin@example.com', 'db_admin', '$2a$12$EXAMPLE_BCRYPT_HASH_FOR_ADMIN_PASSWORD', TRUE, 'system', 'system')
ON CONFLICT (username) DO NOTHING; -- Prevents re-insertion on subsequent runs

-- Assign ROLE_DB_ADMIN to the default admin user
INSERT INTO user_role (user_id, role)
SELECT user_id, 'ROLE_DB_ADMIN'
FROM app_user
WHERE username = 'db_admin'
ON CONFLICT (user_id, role) DO NOTHING;