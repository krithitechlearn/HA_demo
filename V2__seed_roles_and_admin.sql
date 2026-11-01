-- V2__seed_roles_and_admin.sql
-- Inserts default db_admin user and role mapping
-- Related: LLD Section 5.3, 5.4
-- NOTE: Password hash must be set via environment variable at runtime.
-- The placeholder below is a BCrypt hash of 'ChangeMe@12345' - MUST be replaced in production.

INSERT INTO app_user (
    user_id,
    email,
    username,
    password_hash,
    enabled,
    failed_login_attempts,
    created_by,
    updated_by
) VALUES (
    gen_random_uuid(),
    'admin@shoreexcursions.com',
    'admin',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J/HS.iK2i',
    TRUE,
    0,
    'SYSTEM',
    'SYSTEM'
) ON CONFLICT (username) DO NOTHING;

INSERT INTO user_role (user_id, role)
SELECT user_id, 'ROLE_DB_ADMIN'
FROM app_user
WHERE username = 'admin'
ON CONFLICT DO NOTHING;
